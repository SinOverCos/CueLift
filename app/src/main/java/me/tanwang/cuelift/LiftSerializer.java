package me.tanwang.cuelift;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class LiftSerializer {

    private Context context;
    private String filename;

    public LiftSerializer(Context context, String filename) {
        this.context = context;
        this.filename = filename;
    }

    public ArrayList<Lift> loadLifts() throws IOException, JSONException {
        ArrayList<Lift> lifts = new ArrayList<Lift>();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = context.openFileInput(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < jsonArray.length(); i++) {
                lifts.add(new Lift(jsonArray.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // ignore - no file means no previous entries, so leave list empty
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return lifts;
    }

    public void saveLifts(ArrayList<Lift> lifts) throws JSONException, IOException {
        JSONArray jsonArray = new JSONArray();
        for(Lift lift : lifts) {
            jsonArray.put(lift.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
