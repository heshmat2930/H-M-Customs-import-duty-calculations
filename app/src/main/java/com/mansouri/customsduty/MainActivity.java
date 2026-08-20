package com.mansouri.customsduty;

import android.app.*;
import android.os.*;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import org.json.*;

public class MainActivity extends Activity {
    LinearLayout root, form, resultBox;
    Spinner currency, term, fromCur, toCur;
    EditText name, tariff, buy, transport, otherTransport, insurance, otherInsurance, duty, convAmount;
    TextView rate, total, onlineStatus;
    Map<String,Double> customsRates = new LinkedHashMap<>();
    final String[] currencies={"دلار آمریکا","یورو","درهم امارات","یوان چین","ین ژاپن","منات آذربایجان","ریال عمان","لیر ترکیه","روبل روسیه","دینار عراق","روپیه پاکستان","روپیه هند","فرانک سوییس","دینار کویت"};
    final String[] terms={"EXW","FCA","FAS","FOB","CPT","CFR","DDU","DDP","DAP","DPU","CIP","CIF"};
    TextView convResult;
    int blue=Color.rgb(13,71,161), dark=Color.rgb(18,32,56), bg=Color.rgb(246,248,252), green=Color.rgb(20,120,85);

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        customsRates.put("دلار آمریکا",1310661d); customsRates.put("یورو",1551964d); customsRates.put("درهم امارات",356885d); customsRates.put("یوان چین",188931d); customsRates.put("ین ژاپن",8452.85); customsRates.put("منات آذربایجان",770852d); customsRates.put("ریال عمان",3407574d); customsRates.put("لیر ترکیه",30114d); customsRates.put("روبل روسیه",17113d); customsRates.put("دینار عراق",999.56); customsRates.put("روپیه پاکستان",4685.88); customsRates.put("روپیه هند",14386d); customsRates.put("فرانک سوییس",1691667d); customsRates.put("دینار کویت",4272304d);
        build();
    }
    TextView tv(String s,int sp){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(dark);t.setPadding(18,10,18,10);return t;}
    TextView title(String s){TextView t=tv(s,20);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setTextColor(dark);t.setPadding(20,22,20,12);return t;}
    GradientDrawable bg(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(radius);return g;}
    EditText input(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextSize(16);e.setPadding(16,4,16,4);e.setGravity(Gravity.RIGHT);e.setSingleLine(true);e.setTextColor(dark);e.setBackground(bg(Color.WHITE,18));return e;}
    LinearLayout row(String label, EditText e){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);TextView l=tv(label,15);l.setTypeface(Typeface.DEFAULT,Typeface.BOLD);r.addView(l,new LinearLayout.LayoutParams(0,62,1));r.addView(e,new LinearLayout.LayoutParams(0,62,1));r.setPadding(6,4,6,4);return r;}
    void build(){
        updateOnlineRates();
        ScrollView sv=new ScrollView(this); sv.setFillViewport(true);
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(12,0,12,28);root.setBackgroundColor(bg);sv.addView(root);setContentView(sv);
        header(); quickCards(); basic(); costs(); calc(); results(); converter(); about();
    }
    void header(){
        LinearLayout h=new LinearLayout(this);h.setOrientation(LinearLayout.HORIZONTAL);h.setGravity(Gravity.CENTER_VERTICAL);h.setPadding(8,20,8,18);h.setBackground(bg(blue,28));
        TextView menu=tv("☰",28);menu.setTextColor(Color.WHITE);menu.setGravity(Gravity.CENTER);menu.setOnClickListener(v->showSettings());
        LinearLayout center=new LinearLayout(this);center.setOrientation(LinearLayout.VERTICAL);center.setGravity(Gravity.CENTER);
        TextView a=tv("H&M Customs",22);a.setTextColor(Color.WHITE);a.setTypeface(Typeface.DEFAULT,Typeface.BOLD);a.setGravity(Gravity.CENTER);
        TextView b=tv("محاسبه حقوق ورودی کالا",14);b.setTextColor(Color.WHITE);b.setGravity(Gravity.CENTER);center.addView(a);center.addView(b);
        TextView gear=tv("⚙",24);gear.setTextColor(Color.WHITE);gear.setGravity(Gravity.CENTER);gear.setOnClickListener(v->showSettings());
        h.addView(menu,new LinearLayout.LayoutParams(55,80));h.addView(center,new LinearLayout.LayoutParams(0,80,1));h.addView(gear,new LinearLayout.LayoutParams(55,80));root.addView(h);
    }
    void quickCards(){
        LinearLayout cards=new LinearLayout(this);cards.setPadding(0,12,0,4);cards.setGravity(Gravity.CENTER);cards.addView(card("محاسبه\nحقوق ورودی", "🧮"),new LinearLayout.LayoutParams(0,100,1));cards.addView(card("تبدیل\nارز", "💱"),new LinearLayout.LayoutParams(0,100,1));cards.addView(card("درباره\nما", "ℹ"),new LinearLayout.LayoutParams(0,100,1));root.addView(cards);
    }
    TextView card(String text,String icon){TextView c=tv(icon+"\n"+text,15);c.setGravity(Gravity.CENTER);c.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.setBackground(bg(Color.WHITE,22));c.setOnClickListener(v->{if(text.contains("تبدیل"))showConverter();else if(text.contains("درباره"))showAbout();else name.requestFocus();});return c;}
    void section(String s){root.addView(title(s));}
    void basic(){
        section("اطلاعات کالا");form=new LinearLayout(this);form.setOrientation(LinearLayout.VERTICAL);root.addView(form);
        name=input("مثال: تلفن همراه هوشمند");tariff=input("مثال: 8517131000");form.addView(row("نام کالا",name));form.addView(row("شماره تعرفه HS",tariff));
        currency=new Spinner(this);currency.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,currencies));form.addView(rowSpinner("ارز",currency));
        rate=tv("نرخ ارز گمرکی: "+fmt(customsRates.get(currencies[1]))+" ریال",14);rate.setTextColor(blue);form.addView(rate);currency.setSelection(1);currency.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){rate.setText("نرخ ارز گمرکی: "+fmt(customsRates.get(currencies[pos]))+" ریال");}});
        term=new Spinner(this);term.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,terms));form.addView(rowSpinner("ترم تحویل",term));term.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){applyTerm();}});
    }
    LinearLayout rowSpinner(String label,Spinner s){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(tv(label,15),new LinearLayout.LayoutParams(0,62,1));r.addView(s,new LinearLayout.LayoutParams(0,62,1));return r;}
    void costs(){section("هزینه‌ها و حقوق ورودی");buy=input("مبلغ به ارز انتخابی");transport=input("مبلغ به ارز انتخابی");otherTransport=input("مبلغ به ارز انتخابی");insurance=input("مبلغ به ارز انتخابی");otherInsurance=input("مبلغ به ارز انتخابی");duty=input("مثلاً 5");form.addView(row("قیمت خرید",buy));form.addView(row("هزینه حمل و نقل",transport));form.addView(row("حمل و نقل متفرقه",otherTransport));form.addView(row("هزینه بیمه",insurance));form.addView(row("بیمه متفرقه",otherInsurance));form.addView(row("ماخذ حقوق ورودی (%)",duty));}
    void applyTerm(){if(term==null)return;String t=(String)term.getSelectedItem();boolean base=t.equals("EXW")||t.equals("FCA")||t.equals("FAS")||t.equals("FOB");boolean cpt=t.equals("CPT")||t.equals("CFR")||t.equals("DDU")||t.equals("DDP")||t.equals("DAP")||t.equals("DPU");boolean cip=t.equals("CIP")||t.equals("CIF");setField(transport,!base,base?"":"1");setField(otherTransport,true,null);setField(insurance,!base,base?"":"1");setField(otherInsurance,true,cpt?"1":null);if(cip){otherTransport.setText("1");otherInsurance.setText("1");}}
    void setField(EditText e,boolean enabled,String val){e.setEnabled(enabled);if(val!=null)e.setText(val);}
    void calc(){Button b=new Button(this);b.setText("محاسبه حقوق ورودی  ▶");b.setTextSize(18);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setBackground(bg(blue,20));b.setOnClickListener(v->calculate());LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,65);p.setMargins(6,14,6,8);form.addView(b,p);}
    void results(){resultBox=new LinearLayout(this);resultBox.setOrientation(LinearLayout.VERTICAL);resultBox.setPadding(16,16,16,16);resultBox.setBackground(bg(Color.rgb(232,248,241),22));root.addView(title("نتیجه محاسبه"));root.addView(resultBox);total=tv("مجموع حقوق ورودی: 0 تومان",20);total.setTextColor(green);resultBox.addView(total);LinearLayout btns=new LinearLayout(this);Button pdf=new Button(this);pdf.setText("ذخیره PDF");pdf.setAllCaps(false);pdf.setOnClickListener(v->savePdf());Button img=new Button(this);img.setText("ذخیره عکس");img.setAllCaps(false);img.setOnClickListener(v->saveTextImage());btns.addView(pdf,new LinearLayout.LayoutParams(0,60,1));btns.addView(img,new LinearLayout.LayoutParams(0,60,1));root.addView(btns);}
    double num(EditText e){try{return Double.parseDouble(e.getText().toString().replace(",",""));}catch(Exception x){return 0;}}
    void calculate(){double r=customsRates.get(currencies[currency.getSelectedItemPosition()]);double cif=(num(buy)+num(transport)+num(otherTransport)+num(insurance)+num(otherInsurance))*r;double d=cif*num(duty)/100;double vat=(cif+d)*10/100;double hilal=d/100;double waste=cif*0.5/1000;double twelve=cif*0.012;double alih=(cif+d)*2/100;double sum=d+vat+hilal+waste+twelve+alih;resultBox.removeAllViews();resultBox.addView(tv("ارزش سیف: "+fmt(cif)+" تومان",16));resultBox.addView(tv("حقوق ورودی: "+fmt(d)+" تومان",16));resultBox.addView(tv("مالیات بر ارزش افزوده: "+fmt(vat)+" تومان",16));resultBox.addView(tv("هلال احمر: "+fmt(hilal)+" تومان",16));resultBox.addView(tv("پسماند: "+fmt(waste)+" تومان",16));resultBox.addView(tv("دوازده در هزار: "+fmt(twelve)+" تومان",16));resultBox.addView(tv("مالیات علی الحساب: "+fmt(alih)+" تومان",16));total=tv("مجموع حقوق ورودی: "+fmt(sum)+" تومان",20);total.setTextColor(green);total.setTypeface(Typeface.DEFAULT,Typeface.BOLD);resultBox.addView(total);}
    void converter(){section("تبدیل ارز");LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(14,10,14,14);box.setBackground(bg(Color.rgb(239,243,255),22));box.addView(tv("ماشین حساب تبدیل ارز",18));LinearLayout sp=new LinearLayout(this);fromCur=new Spinner(this);toCur=new Spinner(this);String[] cc={"USD - دلار","EUR - یورو","AED - درهم","CNY - یوان","TRY - لیر","AZN - منات","IRR - تومان"};ArrayAdapter<String>a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,cc);fromCur.setAdapter(a);toCur.setAdapter(a);sp.addView(fromCur,new LinearLayout.LayoutParams(0,55,1));sp.addView(toCur,new LinearLayout.LayoutParams(0,55,1));box.addView(sp);convAmount=input("مبلغ");convResult=tv("نتیجه: —",18);box.addView(convAmount);box.addView(convResult);Button cb=new Button(this);cb.setText("تبدیل");cb.setAllCaps(false);cb.setOnClickListener(v->convert());box.addView(cb);onlineStatus=tv("● آخرین نرخ ذخیره‌شده / بدون اینترنت",13);box.addView(onlineStatus);root.addView(box);}
    void showConverter(){convAmount.requestFocus();((android.view.inputmethod.InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(convAmount,android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);}
    void convert(){double x=num(convAmount);String f=(String)fromCur.getSelectedItem(),t=(String)toCur.getSelectedItem();double rf=rateFor(f),rt=rateFor(t);if(rf>0&&rt>0)convResult.setText("نتیجه: "+fmt(x*rf/rt));}
    double rateFor(String s){String code=s.substring(0,3);SharedPreferences sp=getSharedPreferences("rates",MODE_PRIVATE);if(sp.contains(code)){double v=sp.getFloat(code,0);if(v>0)return v;}if(code.equals("USD"))return 1;if(code.equals("EUR"))return .92;if(code.equals("AED"))return 3.67;if(code.equals("CNY"))return 7.2;if(code.equals("TRY"))return 35;if(code.equals("AZN"))return 1.70;return 1;}
    void about(){section("درباره ما");TextView a=tv("کارگزار رسمی گمرک\nترخیص انواع کالا از گمرک بازرگان و سایر گمرکهای شمال غربی\n\nحشمت اله منصوری\n09143612930",16);a.setBackground(bg(Color.WHITE,22));a.setPadding(20,20,20,20);a.setOnClickListener(v->showAbout());root.addView(a);}
    void showAbout(){new AlertDialog.Builder(this).setTitle("H&M Customs").setMessage("کارگزار رسمی گمرک\n\nترخیص انواع کالا از گمرک بازرگان و سایر گمرکهای شمال غربی\n\nحشمت اله منصوری\n09143612930").setPositiveButton("تماس",(d,w)->{try{startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:09143612930")));}catch(Exception e){}}).setNegativeButton("بستن",null).show();}
    void showSettings(){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(25,10,25,10);TextView about=tv("ℹ  درباره ما",18);TextView call=tv("☎  تماس با ما",18);TextView reset=tv("↻  بازنشانی فرم",18);box.addView(about);box.addView(call);box.addView(reset);AlertDialog d=new AlertDialog.Builder(this).setTitle("تنظیمات").setView(box).setNegativeButton("بستن",null).create();about.setOnClickListener(v->{d.dismiss();showAbout();});call.setOnClickListener(v->{try{startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:09143612930")));}catch(Exception e){}});reset.setOnClickListener(v->{name.setText("");tariff.setText("");buy.setText("");transport.setText("");otherTransport.setText("");insurance.setText("");otherInsurance.setText("");duty.setText("");resultBox.removeAllViews();total=tv("مجموع حقوق ورودی: 0 تومان",20);total.setTextColor(green);resultBox.addView(total);d.dismiss();});d.show();}
    void updateOnlineRates(){new Thread(()->{try{URL u=new URL("https://open.er-api.com/v6/latest/USD");HttpURLConnection c=(HttpURLConnection)u.openConnection();c.setConnectTimeout(5000);c.setReadTimeout(5000);BufferedReader br=new BufferedReader(new InputStreamReader(c.getInputStream()));StringBuilder sb=new StringBuilder();String line;while((line=br.readLine())!=null)sb.append(line);br.close();JSONObject j=new JSONObject(sb.toString());JSONObject r=j.getJSONObject("rates");SharedPreferences sp=getSharedPreferences("rates",MODE_PRIVATE);SharedPreferences.Editor ed=sp.edit();String[] codes={"USD","EUR","AED","CNY","TRY","AZN","IRR"};for(String code:codes)if(r.has(code))ed.putFloat(code,(float)r.getDouble(code));ed.putLong("time",System.currentTimeMillis());ed.apply();runOnUiThread(()->{if(onlineStatus!=null)onlineStatus.setText("● نرخ آنلاین به‌روز شد");});}catch(Exception e){runOnUiThread(()->{if(onlineStatus!=null)onlineStatus.setText("● اینترنت در دسترس نیست؛ آخرین نرخ ذخیره‌شده استفاده می‌شود");});}}).start();}
    String fmt(double x){return new DecimalFormat("#,##0.##").format(x);}
    void saveTextImage(){try{resultBox.setDrawingCacheEnabled(true);Bitmap b=Bitmap.createBitmap(resultBox.getDrawingCache());resultBox.setDrawingCacheEnabled(false);String n="customs-result-"+System.currentTimeMillis()+".png";if(Build.VERSION.SDK_INT>=29){ContentValues v=new ContentValues();v.put(MediaStore.Images.Media.DISPLAY_NAME,n);v.put(MediaStore.Images.Media.MIME_TYPE,"image/png");v.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/CustomsDuty");Uri u=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,v);OutputStream o=getContentResolver().openOutputStream(u);b.compress(Bitmap.CompressFormat.PNG,100,o);o.close();}Toast.makeText(this,"تصویر نتیجه ذخیره شد",Toast.LENGTH_LONG).show();}catch(Exception e){Toast.makeText(this,"خطا در ذخیره تصویر",Toast.LENGTH_LONG).show();}}
    void savePdf(){try{PdfDocument doc=new PdfDocument();PdfDocument.PageInfo pi=new PdfDocument.PageInfo.Builder(595,842,1).create();PdfDocument.Page p=doc.startPage(pi);android.graphics.Paint paint=new android.graphics.Paint();paint.setTextSize(20);p.getCanvas().drawText("H&M Customs - Import Duty",60,70,paint);paint.setTextSize(16);p.getCanvas().drawText(total.getText().toString(),60,110,paint);doc.finishPage(p);File f=new File(getExternalFilesDir(null),"customs-result.pdf");FileOutputStream o=new FileOutputStream(f);doc.writeTo(o);o.close();doc.close();Toast.makeText(this,"PDF ذخیره شد",Toast.LENGTH_LONG).show();}catch(Exception e){Toast.makeText(this,"خطا در ساخت PDF",Toast.LENGTH_LONG).show();}}
}
