package com.example.vangelis.connectingdevices.not_used.async;

import android.os.AsyncTask;

public class MyAsyncTask extends AsyncTask<String, Void, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    private AsyncResponse delegate;


    public MyAsyncTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
