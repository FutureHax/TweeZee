package com.t3hh4xx0r.tweezee.twitter;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.t3hh4xx0r.tweezee.ActivityTask;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;

public class TwitterAuth extends SherlockActivity {

	public static final String PREFS = "TwitterOAUTH";
	static SharedPreferences prefs;

	public static CommonsHttpOAuthProvider getOAuthProvider() {
		return new CommonsHttpOAuthProvider(
				"https://api.twitter.com/oauth/request_token",
				"https://api.twitter.com/oauth/access_token",
				"https://api.twitter.com/oauth/authorize");
	}

	public static CommonsHttpOAuthConsumer getOAuthConsumer(Context context) {
		final CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(
				OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
		prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

		consumer.setTokenWithSecret(prefs.getString("oauth_token", null),
				prefs.getString("oauth_token_secret", null));

		return consumer;
	}

	private ActivityTask<TwitterAuth> task = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.auth);

		final String callback = "https://shitballs.com";
		final OAuthProvider provider = getOAuthProvider();
		final OAuthConsumer consumer = getOAuthConsumer(this);

		final WebView browser = (WebView) findViewById(R.id.browser);
		{
			final WebSettings settings = browser.getSettings();
			settings.setJavaScriptEnabled(true);
			settings.setSavePassword(false);
			settings.setSaveFormData(false);
		}

		browser.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				shouldOverrideUrlLoading(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					final String url) {

				if (url.startsWith(callback)) {
					browser.setVisibility(View.INVISIBLE);
					browser.stopLoading();
					browser.loadUrl("about:blank");

					task = new ActivityTask<TwitterAuth>(TwitterAuth.this) {

						@Override
						protected void run() {
							try {
								provider.retrieveAccessToken(
										consumer,
										Uri.parse(url).getQueryParameter(
												"oauth_verifier"));
							} catch (OAuthMessageSignerException e) {
								throw new RuntimeException(e);
							} catch (OAuthNotAuthorizedException e) {
								throw new RuntimeException(e);
							} catch (OAuthExpectationFailedException e) {
								throw new RuntimeException(e);
							} catch (OAuthCommunicationException e) {
								finish(new Finisher<TwitterAuth>() {
									@Override
									public void finish(TwitterAuth activity) {
										activity.showDialog(DIALOG_NETWORK_ERROR);
									}
								});
								return;
							}

							SharedPreferences.Editor editor = prefs.edit();
							HttpParameters p = provider.getResponseParameters();

							final String username = p.getFirst("screen_name");
							DBAdapter db = new DBAdapter(getBaseContext());
							boolean contains = false;
							db.open();
							Cursor c = db.getAllTUsers();
							try {
								while (c.moveToNext()) {
									if (c.getString(
											c.getColumnIndex("username"))
											.equals(username)) {
										contains = true;
									}
								}
							} catch (Exception e) {
							}
							if (!contains) {
								db.insertTUser(username, p.getFirst("user_id"),
										consumer.getToken(),
										consumer.getTokenSecret());
							}
							c.close();
							db.close();

							editor.putString("oauth_token_secret",
									consumer.getTokenSecret());
							editor.putString("oauth_token", consumer.getToken());
							editor.remove("password");

							editor.commit();

							finish(new ActivityTask.Finisher<TwitterAuth>() {
								@Override
								public void finish(TwitterAuth activity) {
									activity.finish();
								}
							});
						}

						@Override
						protected ProgressDialog makeProgressDialog(
								final TwitterAuth activity) {

							final ProgressDialog pd = new ProgressDialog(
									activity);
							pd.setMessage(activity
									.getString(R.string.fetching_access_token));
							pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

								@Override
								public void onCancel(DialogInterface dialog) {
									activity.showDialog(DIALOG_INCOMPLETE);
								}
							});
							return pd;
						}

					};

					return true;
				}
				return false;
			}

		});

		task = (ActivityTask<TwitterAuth>) getLastNonConfigurationInstance();
		if (task != null) {
			task.newActivity(this);
			return;
		}

		if (savedInstanceState != null) {
			browser.restoreState(savedInstanceState);
			browser.setVisibility(View.VISIBLE);
			return;
		}

		task = new ActivityTask<TwitterAuth>(this) {

			@Override
			protected void run() {
				String authorizationURL = null;
				try {
					authorizationURL = provider.retrieveRequestToken(consumer,
							callback);
				} catch (OAuthMessageSignerException e) {
					throw new RuntimeException(e);
				} catch (OAuthNotAuthorizedException e) {
					throw new RuntimeException(e);
				} catch (OAuthExpectationFailedException e) {
					throw new RuntimeException(e);
				} catch (OAuthCommunicationException e) {
					finish(new Finisher<TwitterAuth>() {
						@Override
						public void finish(TwitterAuth activity) {
							activity.showDialog(DIALOG_NETWORK_ERROR);
						}
					});
				}

				assert authorizationURL != null;

				final String fAuthorizationURL = authorizationURL;
				finish(new ActivityTask.Finisher<TwitterAuth>() {
					@Override
					public void finish(TwitterAuth activity) {
						final WebView wv = (WebView) activity
								.findViewById(R.id.browser);
						wv.loadUrl(fAuthorizationURL);
						wv.setVisibility(View.VISIBLE);
						activity.task = null;
					}
				});
			}

			@Override
			protected ProgressDialog makeProgressDialog(
					final TwitterAuth activity) {

				final ProgressDialog pd = new ProgressDialog(activity);
				pd.setMessage(activity
						.getString(R.string.fetching_request_token));
				pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						activity.showDialog(DIALOG_INCOMPLETE);
					}
				});
				return pd;
			}

		};
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		((WebView) findViewById(R.id.browser)).saveState(outState);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return task;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(DIALOG_INCOMPLETE);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private static final int DIALOG_INCOMPLETE = 0;
	private static final int DIALOG_NETWORK_ERROR = 1;

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_INCOMPLETE) {
			return new AlertDialog.Builder(this)
					.setMessage(R.string.auth_not_completed)
					.setPositiveButton(R.string.cancel_auth,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNegativeButton(R.string.dont_cancel_auth, null)
					.setCancelable(false).create();
		} else if (id == DIALOG_NETWORK_ERROR) {
			return new AlertDialog.Builder(this)
					.setMessage(R.string.auth_network_error)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									finish();
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface arg0) {
									finish();
								}
							}).create();
		} else {
			assert false;
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (task != null) {
			task.activityDestroyed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent hi = new Intent(this, MainActivity.class);
			hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(hi);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}