package mirabbas.smartschedule;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import mirabbas.smartschedule.activities.MainTeacherActivity;

/**
 * Created by о on 11.04.2018.
 */

public class LectureHallAdapter extends ArrayAdapter {
    LinkedHashMap<String, ArrayList<Boolean>> map;
    Context context;
    ArrayList<Integer> objects;
    public LectureHallAdapter(Context context, int resource, ArrayList<Integer> objects, LinkedHashMap<String, ArrayList<Boolean>> map) {
        super(context, resource, objects);
        this.map = map;
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.lecture_halls_row, parent, false);
        ImageView boardImage = (ImageView) row.findViewById(R.id.icon_board);
        ImageView compImage = (ImageView) row.findViewById(R.id.icon_computer);
        ImageView projectorImage = (ImageView) row.findViewById(R.id.icon_projector);
        TextView numLectureHall = (TextView) row.findViewById(R.id.number_lh);
        numLectureHall.setText(Integer.toString(objects.get(position)));

        for(String numberLectureHall : map.keySet()){
            for(int i = 0; i < map.get(numberLectureHall).size(); i++){
                if(i == 0){
                    if(map.get(numberLectureHall).get(i)) boardImage.setColorFilter(context.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                    else
                        boardImage.setColorFilter(context.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                }
                if(i == 1){
                    if(map.get(numberLectureHall).get(i)) compImage.setColorFilter(context.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                    else
                        compImage.setColorFilter(context.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                }
                if(i == 2){
                    if(map.get(numberLectureHall).get(i)) projectorImage.setColorFilter(context.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                    else
                        projectorImage.setColorFilter(context.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                }
            }
        }
        return row;

    }
}
