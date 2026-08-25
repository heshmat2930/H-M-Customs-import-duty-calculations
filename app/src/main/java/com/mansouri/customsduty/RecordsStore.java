package com.mansouri.customsduty;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RecordsStore {
    private static final String PREFS="customs_records";
    private static final String KEY="records";
    private final Context context;
    public RecordsStore(Context context){this.context=context.getApplicationContext();}
    private JSONArray read(){try{return new JSONArray(context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getString(KEY,"[]"));}catch(Exception e){return new JSONArray();}}
    private void write(JSONArray a){context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString(KEY,a.toString()).apply();}
    public synchronized long add(JSONObject item){try{JSONArray a=read();long id=System.currentTimeMillis();item.put("id",id);a.put(item);write(a);return id;}catch(Exception e){return -1;}}
    public synchronized void update(JSONObject item){try{JSONArray a=read();long id=item.optLong("id",-1);for(int i=0;i<a.length();i++)if(a.getJSONObject(i).optLong("id",-2)==id){a.put(i,item);break;}write(a);}catch(Exception ignored){}}
    public synchronized void delete(long id){try{JSONArray a=read();JSONArray out=new JSONArray();for(int i=0;i<a.length();i++)if(a.getJSONObject(i).optLong("id",-2)!=id)out.put(a.getJSONObject(i));write(out);}catch(Exception ignored){}}
    public synchronized List<JSONObject> all(){List<JSONObject> r=new ArrayList<>();JSONArray a=read();for(int i=a.length()-1;i>=0;i--)try{r.add(a.getJSONObject(i));}catch(Exception ignored){}return r;}
}
