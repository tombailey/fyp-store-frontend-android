package me.tombailey.http;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 20/01/2017.
 */
public class FormBody {

    public static class Param {

        private String mName;
        private Value mValue;

        public Param(String name, Value value) {
            mName = name;
            mValue = value;
        }

        public String getmName() {
            return mName;
        }

        public Value getmValue() {
            return mValue;
        }


        public interface Value {

        }

    }

    public static class Builder {

        private List<Param> params;

        public Builder() {

        }

        public Builder add(String name, Param.Value value) {
            if (params == null) {
                params = new ArrayList<Param>(4);
            }
            params.add(new Param(name, value));
            return this;
        }

    }

}
