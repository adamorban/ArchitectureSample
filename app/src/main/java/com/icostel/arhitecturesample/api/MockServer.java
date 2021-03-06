package com.icostel.arhitecturesample.api;

import android.content.Context;

import com.google.gson.Gson;
import com.icostel.arhitecturesample.api.model.SignInResponse;
import com.icostel.arhitecturesample.utils.AppExecutors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import timber.log.Timber;

@Singleton
public class MockServer {

    private static final String TAG = MockServer.class.getCanonicalName();

    private static final int INTERNAL_ERROR = 500;
    private static final int REQUEST_DURATION = 3;

    private final MockWebServer server = new MockWebServer();
    private final Gson gson = new Gson();
    private final Context context;
    private String url;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Inject
    MockServer(Context context, AppExecutors appExecutors) {
        this.context = context;
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                MockResponse response = new MockResponse().setResponseCode(INTERNAL_ERROR);
                String requestPath = request.getPath();
                Timber.d(TAG + " received request path: " + requestPath);
                if (request.getPath().equals("/users/")) {
                    response = getAllResourcesResponse(GenericResponseData.USERS);
                } else if (request.getPath().equals("/login/")) {
                    response = getLoginResponse();
                }
                Timber.d(TAG + " response body: %s", response.getBody().toString());

                return response;
            }
        };
        server.setDispatcher(dispatcher);
        appExecutors.networkIO().execute(() -> {
            try {
                server.start();
            } catch (IOException e) {
                Timber.d(e);
            }
            url = server.url("/").toString();
            latch.countDown();
        });
    }

    public String getUrl() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Timber.d(e);
        }
        return url;
    }

    private MockResponse getAllResourcesResponse(int resourceType) {
        try {
            return new MockResponse()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody(gson.toJson(getResponseBody(resourceType)))
                    .throttleBody(1024, REQUEST_DURATION, TimeUnit.SECONDS);
        } catch (Exception e) {
            Timber.e(TAG + "getAllUsersResponse(), err: " + e.getMessage());
            return new MockResponse().setResponseCode(INTERNAL_ERROR);
        }
    }

    private MockResponse getLoginResponse() {
        try {
            SignInResponse signInResponse = new SignInResponse(true, "welcome", "sdkjahdds8sd79sd87v8734134ec13re");
            return new MockResponse()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody(gson.toJson(signInResponse))
                    .throttleBody(1024, REQUEST_DURATION, TimeUnit.SECONDS);
        } catch (Exception e) {
            Timber.e(TAG + "getAllUsersResponse(), err: " + e.getMessage());
            return new MockResponse().setResponseCode(INTERNAL_ERROR);
        }
    }

    private GenericResponseData getResponseBody(int resourceType) {
        // get raw json file from /raw
        int usersJsonId = context.getResources().getIdentifier(GenericResponseData.getResource(resourceType), "raw", context.getPackageName());
        Reader jsonReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(usersJsonId)));
        GenericResponseData response;
        switch (resourceType) {
            case GenericResponseData.USERS:
            default:
                response = gson.fromJson(jsonReader, UserResponseData.class);
        }

        return response;
    }
}
