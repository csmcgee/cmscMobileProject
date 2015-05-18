package cmsc491.myplacelist.google;

import java.util.ArrayList;

public class GooglePlaceDetailsResponse {
    public PlaceDetail result;

    public static class PlaceDetail {
        public String formatted_phone_number;
        public String formatted_address;
        public OpeningHours opening_hours;

        public static class OpeningHours {
            public boolean open_now;
            public ArrayList<String> weekday_text;

            public String toString(){
                StringBuilder sb = new StringBuilder();
                if(weekday_text != null){
                    for(int i = 0; i < weekday_text.size(); i++){
                        sb.append(weekday_text.get(i));
                        if((i + 1) != weekday_text.size()){
                            sb.append("\n");
                        }
                    }
                }
                return sb.toString();
            }
        }
    }
}
