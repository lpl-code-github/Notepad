package com.example.notepad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.notepad.R;
import com.example.notepad.bean.NotepadBean;

import java.util.List;

public class NotepadAdapter extends BaseAdapter {

    private Context context;
    private List<NotepadBean> list;


    public NotepadAdapter(Context context){
        this.context = context;
    }

    public void setList(List<NotepadBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 :list.size();
    }

    @Override
    public NotepadBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.notepd_item_layout,null);
            viewHolder  = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        NotepadBean noteInfo = getItem(position);
        viewHolder.tvNoteoadContent.setText(noteInfo.getNotepadContent());
        viewHolder.tcNotepadTime.setText(noteInfo.getNotepadTime());

        return convertView;
    }



    class ViewHolder{
        TextView tvNoteoadContent;
        TextView tcNotepadTime;
        public ViewHolder(View view){
            tvNoteoadContent=(TextView) view.findViewById(R.id.item_content);
            tcNotepadTime=(TextView) view.findViewById(R.id.item_time);
        }
    }
}
