package anuragkondeya.com.anuragkondeya.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class representing a story item. paracable for passing object between fragments
 */

public class Story implements Parcelable {

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
    public String abstractText;  //abstract
    public String body;          //body
    public String headline;      //headline
    public String id;            //id
    public String image;     // image url

    Story(Parcel in) {
        abstractText = in.readString();
        body = in.readString();
        headline = in.readString();
        id = in.readString();
        image = in.readString();
    }

    Story(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(abstractText);
        dest.writeString(body);
        dest.writeString(headline);
        dest.writeString(id);
        dest.writeString(image);

    }


}
