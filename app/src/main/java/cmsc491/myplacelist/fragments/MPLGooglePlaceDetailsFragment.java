package cmsc491.myplacelist.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import cmsc491.myplacelist.R;
import cmsc491.myplacelist.google.GooglePlaceDetailsResponse;
import cmsc491.myplacelist.google.GoogleSearchAO;

public class MPLGooglePlaceDetailsFragment extends Fragment {
    private View v;
    private TextView mAddress, mPhone, mStoreHours;
    private ProgressBar mProgress;
    private String placeId;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_google_place_details, container, false);
        initializeUI();
        new GoogleSearchRequestTask().execute(placeId);

        return v;
    }

    public void setPlaceId(String id){
        this.placeId = id;
    }

    private void initializeUI(){
        mAddress = (TextView) v.findViewById(R.id.gpdAddressValue);
        mPhone = (TextView) v.findViewById(R.id.gpdPhoneValue);
        mStoreHours = (TextView) v.findViewById(R.id.gpdStoreHoursValue);
        mProgress = (ProgressBar) v.findViewById(R.id.gpdProgress);
    }

    private class GoogleSearchRequestTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String ...placeIds) {
            GoogleSearchAO googleSearchAO  = new GoogleSearchAO();
            String detail = googleSearchAO.getPlaceDetailsById(placeIds[0]);
            return detail;
        }

        protected void onPostExecute(String result) {
            GooglePlaceDetailsResponse detail = new Gson().fromJson(result, GooglePlaceDetailsResponse.class);
            if(detail.result.formatted_address != null && !detail.result.formatted_address.isEmpty())
                mAddress.setText(detail.result.formatted_address);

            if(detail.result.formatted_phone_number != null && !detail.result.formatted_phone_number.isEmpty())
                mPhone.setText(detail.result.formatted_phone_number);

            if(detail.result.opening_hours != null && detail.result.opening_hours.weekday_text != null && !detail.result.opening_hours.weekday_text.isEmpty())
                mStoreHours.setText(detail.result.opening_hours.toString());

            mProgress.setVisibility(View.INVISIBLE);
        }
    }

}
