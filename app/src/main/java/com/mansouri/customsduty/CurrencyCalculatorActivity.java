package com.mansouri.customsduty;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.text.*;
import java.text.DecimalFormat;
import java.util.*;

public class CurrencyCalculatorActivity extends Activity {
    final String[] names={"دلار آمریکا","یورو","درهم امارات","یوان چین","ین ژاپن","منات آذربایجان","ریال عمان","لیر ترکیه","روبل روسیه","دینار عراق","روپیه پاکستان","روپیه هند","فرانک سوییس","دینار کویت"};
    final String[] codes={"USD","EUR","AED","CNY","JPY","AZN","OMR","TRY","RUB","IQD","PKR","INR","CHF","KWD"};
    final double[] customs={1310661d,1551964d,356885d,188931d,8452.85d,770852d,3407574d,30114d,17113d,999.56d,4685.88d,14386d,1691667d,4272304d};
    final String[] flags={"🇺🇸","🇪🇺","🇦🇪","🇨🇳","🇯🇵","🇦🇿","🇴🇲","🇹🇷","🇷🇺","🇮🇶","🇵🇰","🇮🇳","🇨🇭","🇰🇼"};
    final int blue=Color.rgb(13,71,161),dark=Color.rgb(18,32,56),page=Color.rgb(246,248,252),green=Color.rgb(20,120,85);
    int fromIndex=0,toIndex=3;
    EditText amount; TextView result,fromFlag,toFlag,fromName,toName,rateInfo,status;
    Switch onlineSwitch;

    @Override public void onCreate(Bundle b){super.onCreate(b); build();}
    GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(r);return g;}
    TextView tv(String s,int n){TextView t=new TextView(this);t.setText(s);t.setTextSize(n);t.setTextColor(dark);t.setGravity(Gravity.CENTER_VERTICAL);t.setPadding(10,5,10,5);return t;}

    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(14,14,14,24);root.setBackgroundColor(page);
        TextView h=tv("ماشین حساب ارز",22);h.setTextColor(Color.WHITE);h.setGravity(Gravity.CENTER);h.setTypeface(null,Typeface.BOLD);h.setBackground(bg(blue,24));root.addView(h,new LinearLayout.LayoutParams(-1,68));
        LinearLayout mode=new LinearLayout(this);mode.setGravity(Gravity.CENTER_VERTICAL);TextView ml=tv("نوع نرخ",15);ml.setTypeface(null,Typeface.BOLD);onlineSwitch=new Switch(this);onlineSwitch.setText("🌐 نرخ آنلاین جهانی");onlineSwitch.setTextSize(15);onlineSwitch.setChecked(false);onlineSwitch.setOnCheckedChangeListener((button,checked)->{if(checked){onlineSwitch.setChecked(false);status.setText("🌐 نرخ آنلاین فعلاً در دست بررسی است");Toast.makeText(this,"دریافت نرخ آنلاین در مرحله بعد فعال می‌شود",Toast.LENGTH_SHORT).show();}else{status.setText("🏛 استفاده از نرخ گمرکی برنامه");update();}});mode.addView(ml,new LinearLayout.LayoutParams(0,58,1));mode.addView(onlineSwitch);root.addView(mode);
        root.addView(makeBox(true),new LinearLayout.LayoutParams(-1,94));
        LinearLayout swr=new LinearLayout(this);swr.setGravity(Gravity.CENTER);Button sw=new Button(this);sw.setText("⇅");sw.setTextSize(20);sw.setAllCaps(false);sw.setTextColor(Color.WHITE);sw.setBackground(bg(blue,28));sw.setOnClickListener(v->{int x=fromIndex;fromIndex=toIndex;toIndex=x;refreshNamesAndFlags();update();});swr.addView(sw,new LinearLayout.LayoutParams(60,50));root.addView(swr,new LinearLayout.LayoutParams(-1,55));
        root.addView(makeBox(false),new LinearLayout.LayoutParams(-1,94));
        rateInfo=tv("",14);rateInfo.setTextColor(blue);rateInfo.setGravity(Gravity.CENTER);root.addView(rateInfo,new LinearLayout.LayoutParams(-1,42));
        status=tv("🏛 استفاده از نرخ گمرکی برنامه",13);status.setTextColor(green);status.setGravity(Gravity.CENTER);root.addView(status,new LinearLayout.LayoutParams(-1,42));
        setContentView(root);
        amount.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){}public void onTextChanged(CharSequence s,int a,int b,int c){update();}public void afterTextChanged(Editable e){}});
        update();refreshNamesAndFlags();
    }

    LinearLayout makeBox(boolean source){
        LinearLayout outer=new LinearLayout(this);outer.setOrientation(LinearLayout.VERTICAL);outer.setPadding(0,0,0,2);
        TextView name=tv(source?names[fromIndex]:names[toIndex],14);name.setGravity(Gravity.CENTER);name.setTypeface(null,Typeface.BOLD);if(source)fromName=name;else toName=name;outer.addView(name,new LinearLayout.LayoutParams(-1,28));
        LinearLayout box=new LinearLayout(this);box.setGravity(Gravity.CENTER_VERTICAL);box.setPadding(8,5,8,5);box.setBackground(bg(Color.WHITE,18));
        TextView flag=tv(source?flags[fromIndex]:flags[toIndex],27);flag.setGravity(Gravity.CENTER);if(source)fromFlag=flag;else toFlag=flag;box.addView(flag,new LinearLayout.LayoutParams(60,58));
        if(source){amount=new EditText(this);amount.setText("1");amount.setTextSize(20);amount.setGravity(Gravity.CENTER);amount.setInputType(2|8192);amount.setSingleLine(true);amount.setBackground(bg(page,14));box.addView(amount,new LinearLayout.LayoutParams(0,58,1));}
        else{result=tv("0",20);result.setTextColor(green);result.setGravity(Gravity.CENTER);result.setTypeface(null,Typeface.BOLD);result.setBackground(bg(Color.rgb(232,248,241),14));box.addView(result,new LinearLayout.LayoutParams(0,58,1));}
        View.OnClickListener choose=v->showCurrencyList(source);flag.setOnClickListener(choose);name.setOnClickListener(choose);box.setOnClickListener(v->{if(v==flag)choose.onClick(v);});outer.addView(box,new LinearLayout.LayoutParams(-1,62));return outer;
    }

    void showCurrencyList(boolean source){
        new AlertDialog.Builder(this).setTitle(source?"انتخاب ارز مبدأ":"انتخاب ارز مقصد").setItems(names,(dialog,which)->{if(source)fromIndex=which;else toIndex=which;refreshNamesAndFlags();update();}).setNegativeButton("لغو",null).show();
    }
    void refreshNamesAndFlags(){if(fromName!=null)fromName.setText(names[fromIndex]);if(toName!=null)toName.setText(names[toIndex]);if(fromFlag!=null)fromFlag.setText(flags[fromIndex]);if(toFlag!=null)toFlag.setText(flags[toIndex]);}
    double rate(){if(fromIndex==toIndex)return 1d;return customs[fromIndex]/customs[toIndex];}
    void update(){if(result==null||amount==null)return;double a=0;try{a=Double.parseDouble(amount.getText().toString().replace(",",""));}catch(Exception e){}double r=rate();result.setText(fmt(a*r));rateInfo.setText("1 "+codes[fromIndex]+" = "+fmt(r)+" "+codes[toIndex]);}
    String fmt(double x){return new DecimalFormat("#,##0.####").format(x);}
}
