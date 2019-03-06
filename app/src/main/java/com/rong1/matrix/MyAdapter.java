package com.rong1.matrix;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
/**
 * Created by rong1 on 2018/9/13.
 */

public class MyAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private String[] s;

    public MyAdapter(Context context, int row,int column) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        s = new String[row * column];
        for (int i = 0; i < row * column; i++) {
            s[i] = "";
        }
    }

    @Override
    public int getCount() {
        return s.length;
    }

    @Override
    public Object getItem(int i) {
        return s[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final int[] index = {-1};
        viewHolder holder = null;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.gridview_item, null);
        }
        holder = new viewHolder();
        holder.edttext = (EditText) view.findViewById(R.id.edtext);

        /*if(view==null){
            view=layoutInflater.inflate(R.layout.gridview_item,null);
            holder =new viewHolder();
            holder.edttext=(EditText)view.findViewById(R.id.edtext);
            //holder.edttext.setText(i+"");
            view.setTag(holder);
       }else{
            holder=(viewHolder)view.getTag();
        }*/

        holder.edttext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    index[0] = i;
                }
                return false;
            }
        });

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            //当text改变之后将内用保存在s数组内
            public void afterTextChanged(Editable editable) {
                s[i] = editable.toString();
            }
        };
        holder.edttext.clearFocus();//防止点击以后弹出键盘，重新getview导致的焦点丢失
        if (index[0] != -1 && index[0] == i) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            holder.edttext.requestFocus();
        }
        holder.edttext.setText(s[i]);//这一定要放在clearFocus()之后，否则最后输入的内容在拉回来时会消失
        //设置EditText的焦点监听器判断焦点变化，当有焦点时addTextChangedListener，失去焦点时removeTextChangedListener
        holder.edttext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText ext = (EditText) view;
                if (b) {
                    //当edttext有焦点时，设置监听器
                    ext.addTextChangedListener(textWatcher);
                } else {
                    ext.removeTextChangedListener(textWatcher);
                }

            }
        });
        //holder.edttext.setText(i+"");
        Log.d("getview方法中", i + "");

        return view;
    }

    public String[] getS() {
        return s;
    }

    public class viewHolder {
        EditText edttext;
    }
}
