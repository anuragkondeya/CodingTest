package anuragkondeya.com.anuragkondeya.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

/**
 * Class representing a story item. paracable for passing object between fragments
 */

public class Story implements Parcelable {

    public String abstractText;  //abstract
    public String body;          //body
    public String headline;      //headline
    public String id;            //id
    public String imageURL;     // image url

    @Override
    public int describeContents() {
        return 0;
    }

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

    Story(Parcel in) {
        abstractText = in.readString();
        body = in.readString();
        headline = in.readString();
        id = in.readString();
        imageURL = in.readString();
    }


    Story(){}


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(abstractText);
        dest.writeString(body);
        dest.writeString(headline);
        dest.writeString(id);
        dest.writeString(imageURL);

    }

}
