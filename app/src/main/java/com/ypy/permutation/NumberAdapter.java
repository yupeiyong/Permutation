package com.ypy.permutation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NumberAdapter extends BaseAdapter {
    private List<List<Integer>> _numbers;
    private Context _context;
    public NumberAdapter(List<List<Integer>>numbers){
        this._numbers=numbers;
    }

    @Override
    public int getCount() {
        if(_numbers==null)
            return 0;
        return _numbers.size();
    }

    @Override
    public Object getItem(int i) {
        if(_numbers==null)
            return null;
        return _numbers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(_context==null){
            _context=viewGroup.getContext();
        }
        if(view==null){
            view=LayoutInflater.from(_context).inflate(R.layout.item_number,viewGroup,false);
            holder=new ViewHolder();
            holder.number=view.findViewById(R.id.number);
            holder.first=view.findViewById(R.id.first);
            holder.second=view.findViewById(R.id.second);
            holder.third=view.findViewById(R.id.third);
            holder.fourth=view.findViewById(R.id.fourth);
            holder.fifth=view.findViewById(R.id.fifth);
            holder.sixth=view.findViewById(R.id.sixth);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }
        List<Integer>numberList=_numbers.get(i);
        if(numberList!=null){
            holder.number.setText(String.valueOf(i+1));
            holder.first.setText(String.valueOf(numberList.get(0)));
            holder.second.setText(String.valueOf(numberList.get(1)));
            holder.third.setText(String.valueOf(numberList.get(2)));
            holder.fourth.setText(String.valueOf(numberList.get(3)));
            holder.fifth.setText(String.valueOf(numberList.get(4)));
            holder.sixth.setText(String.valueOf(numberList.get(5)));
        }
        return view;
    }

    class ViewHolder{
        TextView number;
        TextView first;
        TextView second;
        TextView third;
        TextView fourth;
        TextView fifth;
        TextView sixth;
    }
}
