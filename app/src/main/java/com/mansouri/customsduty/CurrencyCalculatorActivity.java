package com.mansouri.customsduty;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CurrencyCalculatorActivity extends Activity {
    final String[] names={"دلار آمریکا","یورو","درهم امارات","یوان چین","ین ژاپن","منات آذربایجان","ریال عمان","لیر ترکیه","روبل روسیه","دینار عراق","روپیه پاکستان","روپیه هند","فرانک سوییس","دینار کویت"};
    final String[] codes={"USD","EUR","AED","CNY","JPY","AZN","OMR","TRY","RUB","IQD","PKR","INR","CHF","KWD"};
    final double[] customs={1310661d,1551964d,356885d,188931d,8452.85d,770852d,3407574d,30114d,17113d,999.56d,4685.88d,14386d,1691667d,4272304d};
    final String[] flags={"🇺🇸","🇪🇺","🇦🇪","🇨🇳","🇯🇵","🇦🇿","🇴🇲","🇹🇷","🇷🇺","🇮🇶","🇵🇰","🇮🇳","🇨🇭","🇰🇼"};
    final int blue=Color.rgb(13,71,161), dark=Color.rgb(18,32,56), page=Color.rgb(246,248,252), green=Color.rgb(20,120,85);
    Spinner from,to; EditText amount; TextView result,rateInfo,status; Switch onlineSwitch; Map<String,Double> onlineRates=new HashMap<>();
    boolean onlineReady=false;

    @Override public void onCreate(Bundle b){super.onCreate(b); build(); if(onlineSwitch.isChecked()) fetchRates();}
    GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(r);return g;}
    TextView tv(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(dark);t.setPadding(16,10,16,10);return t;}
    TextView label(String s){TextView t=tv(s,14);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    void build(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(14,14,14,24); root.setBackgroundColor(page);
        TextView head=tv("ماشین حساب ارز",22); head.setTextColor(Color.WHITE); head.setGravity(Gravity.CENTER); head.setTypeface(Typeface.DEFAULT,Typeface.BOLD); head.setBackground(bg(blue,24)); root.addView(head,new LinearLayout.LayoutParams(-1,70));
        LinearLayout mode=new LinearLayout(this); mode.setGravity(Gravity.CENTER_VERTICAL); mode.setPadding(8,8,8,4); TextView modeLabel=label("نوع نرخ"); onlineSwitch=new Switch(this); onlineSwitch.setText("نرخ آنلاین جهانی"); onlineSwitch.setTextSize(15); onlineSwitch.setChecked(false); onlineSwitch.setOnCheckedChangeListener((v,checked)->{status.setText(checked?"🌐 دریافت آخرین نرخ آنلاین...":"🏛 استفاده از نرخ گمرکی برنامه"); if(checked)fetchRates(); else {onlineReady=false; rateInfo.setText("نرخ تبدیل بر اساس نرخ گمرکی برنامه"); updateResult();}}); mode.addView(modeLabel,new LinearLayout.LayoutParams(0,60,1)); mode.addView(onlineSwitch); root.addView(mode);
        LinearLayout fromBox=currencyBox("ارز مبدأ",0); root.addView(fromBox,new LinearLayout.LayoutParams(-1,105));
        LinearLayout swap=new LinearLayout(this); swap.setGravity(Gravity.CENTER); Button sw=new Button(this); sw.setText("⇅"); sw.setTextSize(22); sw.setAllCaps(false); sw.setTextColor(Color.WHITE); sw.setBackground(bg(blue,30)); sw.setOnClickListener(v->{int p=from.getSelectedItemPosition();from.setSelection(to.getSelectedItemPosition());to.setSelection(p);updateResult();}); swap.addView(sw,new LinearLayout.LayoutParams(64,58)); root.addView(swap,new LinearLayout.LayoutParams(-1,64));
        LinearLayout toBox=currencyBox("ارز مقصد / نتیجه",1); root.addView(toBox,new LinearLayout.LayoutParams(-1,115));
        LinearLayout info=new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL); info.setPadding(8,12,8,4); rateInfo=tv("نرخ تبدیل بر اساس نرخ گمرکی برنامه",14); rateInfo.setTextColor(blue); status=tv("🏛 استفاده از نرخ گمرکی برنامه",13); status.setTextColor(green); info.addView(rateInfo);info.addView(status);root.addView(info);
        Button refresh=new Button(this);refresh.setText("🔄 بروزرسانی نرخ آنلاین");refresh.setTextSize(15);refresh.setAllCaps(false);refresh.setOnClickListener(v->fetchRates());root.addView(refresh,new LinearLayout.LayoutParams(-1,58));
        setContentView(root); from.setOnItemSelectedListener(listener); to.setOnItemSelectedListener(listener); amount.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){updateResult();}public void afterTextChanged(android.text.Editable e){}}); updateResult();
    }
    LinearLayout currencyBox(String title,int type){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.HORIZONTAL);box.setGravity(Gravity.CENTER_VERTICAL);box.setPadding(10,6,10,6);box.setBackground(bg(Color.WHITE,20));
        TextView flag=tv(type==0?flags[0]:flags[1],27); flag.setGravity(Gravity.CENTER); box.addView(flag,new LinearLayout.LayoutParams(48,-1));
        LinearLayout middle=new LinearLayout(this);middle.setOrientation(LinearLayout.VERTICAL); TextView l=label(title);middle.addView(l);
        if(type==0){from=new Spinner(this);from.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names));middle.addView(from,new LinearLayout.LayoutParams(-1,48));}
        else {to=new Spinner(this);to.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names));to.setSelection(3);middle.addView(to,new LinearLayout.LayoutParams(-1,48));}
        box.addView(middle,new LinearLayout.LayoutParams(0,-1,1));
        if(type==0){amount=new EditText(this);amount.setText("1");amount.setTextSize(20);amount.setGravity(Gravity.CENTER);amount.setInputType(2|8192);amount.setSingleLine(true);amount.setSelectAllOnFocus(true);amount.setBackground(bg(Color.rgb(246,248,252),16));box.addView(amount,new LinearLayout.LayoutParams(125,58));}
        else {result=tv("0",20);result.setTextColor(green);result.setTypeface(Typeface.DEFAULT,Typeface.BOLD);result.setGravity(Gravity.CENTER);result.setBackground(bg(Color.rgb(232,248,241),16));box.addView(result,new LinearLayout.LayoutParams(125,58));}
        return box;
    }
    AdapterView.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int pos,long id){updateResult();}};
    void updateResult(){if(from==null||to==null||amount==null||result==null)return;double a=0;try{a=Double.parseDouble(amount.getText().toString().replace(",",""));}catch(Exception e){}double r=conversionRate();double out=a*r;result.setText(format(out));String f=codes[from.getSelectedItemPosition()], t=codes[to.getSelectedItemPosition()];rateInfo.setText("1 "+f+" = "+formatRate(r)+" "+t);}
    double conversionRate(){int a=from.getSelectedItemPosition(),b=to.getSelectedItemPosition();if(a==b)return 1; if(onlineSwitch!=null&&onlineSwitch.isChecked()&&onlineReady){double rf=onlineRates.get(codes[a]), rt=onlineRates.get(codes[b]);return rt/rf;} return customs[a]/customs[b];}
    String format(double x){return new java.text.DecimalFormat("#,##0.####").format(x);}
    String formatRate(double x){return new java.text.DecimalFormat("#,##0.####").format(x);}
    void fetchRates(){status.setText("🌐 دریافت آخرین نرخ آنلاین...");new Thread(()->{try{URL u=new URL("https://open.er-api.com/v6/latest/USD");HttpURLConnection c=(HttpURLConnection)u.openConnection();c.setConnectTimeout(10000);c.setReadTimeout(10000);c.setRequestMethod("GET");BufferedReader r=new BufferedReader(new InputStreamReader(c.getInputStream()));StringBuilder s=new StringBuilder();String line;while((line=r.readLine())!=null)s.append(line);JSONObject o=new JSONObject(s.toString());JSONObject rates=o.getJSONObject("rates");HashMap<String,Double> tmp=new HashMap<>();for(String code:codes)tmp.put(code,rates.getDouble(code));onlineRates=tmp;onlineReady=true;runOnUiThread(()->{status.setText("🌐 آخرین نرخ آنلاین دریافت شد • "+new SimpleDateFormat("yyyy/MM/dd HH:mm",Locale.US).format(new Date()));updateResult();});}catch(Exception e){onlineReady=false;runOnUiThread(()->{status.setText("⚠ دریافت نرخ آنلاین ناموفق بود؛ نرخ گمرکی فعال ماند");updateResult();});}}).start();}
}
