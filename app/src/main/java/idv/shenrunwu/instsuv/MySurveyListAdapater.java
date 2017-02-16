package idv.shenrunwu.instsuv;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by shenrunwu on 2016/11/16.
 */

public class MySurveyListAdapater extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<StartActivity.MySurveyListItem> lists;




    private class ViewHolder {
        TextView mySurveyEndtime;
        TextView mySurveyTopic;
        TextView mySurveyInProsses;

        public ViewHolder(TextView endTime
                , TextView topic, TextView inProsses) {
            mySurveyEndtime=endTime;
            mySurveyTopic=topic;
            mySurveyInProsses=inProsses;

        }
    }

    @Override
    public long getItemId(int position) {
        return lists.indexOf(getItem(position));
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    public MySurveyListAdapater(Context context, List<StartActivity.MySurveyListItem> mySurveyListItems) {
        layoutInflater = LayoutInflater.from(context);
        this.lists = mySurveyListItems;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MySurveyListAdapater.ViewHolder holder=null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.mysurveylist, null);
            holder = new MySurveyListAdapater.ViewHolder(
                    (TextView) convertView.findViewById(R.id.mySurveyEndTime),
                    (TextView) convertView.findViewById(R.id.mySurveyTopic),
                    (TextView) convertView.findViewById(R.id.mySurveyInProsses)
            );
            convertView.setTag(holder);
        } else {
            holder = (MySurveyListAdapater.ViewHolder) convertView.getTag();
        }

        StartActivity.MySurveyListItem mySurveyListItem = (StartActivity.MySurveyListItem) getItem(position);

        Date endDate=new Date(mySurveyListItem.endOfSurveyTime);
        Date nowDate=new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        holder.mySurveyEndtime.setText("結束時間："+dateFormat.format(endDate));
        holder.mySurveyTopic.setText(mySurveyListItem.topic);


        if (endDate.getTime()>nowDate.getTime()){
            holder.mySurveyInProsses.setBackgroundColor(Color.YELLOW);
            holder.mySurveyInProsses.setText(mySurveyListItem.showID);
        }else{
            holder.mySurveyInProsses.setText("End");
        }

        return convertView;
    }


}
