package com.ypy.permutation;

import android.os.Build;
import android.util.ArrayMap;

import androidx.annotation.RequiresApi;

import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ModelStorage<TModel> {
    private Map<Integer,TModel> map=new ArrayMap<>();

    public static ModelStorage getInstance(){
        return SingletonHolder.instance;
    }

    private static class SingletonHolder{
        private static final ModelStorage instance=new ModelStorage();
    }

    public void putModel(TModel model,int key){
        if(map.containsKey(key)){
            map.remove(key);
        }
        map.put(key,model);
    }

    public TModel getModel(int key){
        if(map.size()==0){
            return null;
        }else{
            return map.remove(key);
        }
    }
}
