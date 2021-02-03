/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.core.utils.Auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xutil.display.ScreenUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import cn.dreamn.qianji_auto.core.db.AutoBill;
import cn.dreamn.qianji_auto.core.db.Cache;
import cn.dreamn.qianji_auto.core.utils.AutoBills;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.Caches;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.ui.floats.AutoFloat;
import cn.dreamn.qianji_auto.ui.floats.AutoFloatTip;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.Permission;

import static cn.dreamn.qianji_auto.core.utils.Tools.goUrl;


public class CallAutoActivity {

    public  static  void call(Context context, BillInfo billInfo){
        if(!billInfo.isAvaiable())return;

        AutoBills.add(billInfo);
        Tasker.add(context,billInfo);

    }

    public static void setTimeout(String timeout){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("auto_timeout",timeout);
    }
    public static void setCheck(boolean check){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("auto_check",check);
    }
    //角标超时
    public static String getTimeout(){
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("auto_timeout","10");
    }
    //角标检查
    public static Boolean getCheck(){
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getBoolean("auto_check",true);
    }
    //显示角标
    public static void showTip(Context context,BillInfo billInfo){
        try {
            Logs.d("唤起自动记账面板角标");
            AutoFloatTip autoFloatTip=new AutoFloatTip(context);
            autoFloatTip.setData(billInfo);
            autoFloatTip.setWindowManagerParams( ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight()/2-100,700,200);
            autoFloatTip.show();
        }catch (Exception e){
            Logs.i("请授予悬浮窗权限！"+e.toString());
            ToastUtils.toast("请授予悬浮窗权限！");
            Caches.AddOrUpdate("float_lock","false");
        }
    }
    //显示悬浮记账
    public static void showFloat(Context context,BillInfo billInfo){
        try {
            Logs.d("唤起自动记账面板");
            AutoFloat autoFloat=new AutoFloat(context);
            autoFloat.setData(billInfo);
            autoFloat.setWindowManagerParams(0,0, ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight());
            autoFloat.show();
        }catch (Exception e){
            Logs.i("请授予悬浮窗权限！"+e.toString());
            ToastUtils.toast("请授予悬浮窗权限！");
            Caches.AddOrUpdate("float_lock","false");
        }

    }

    public static void goQianji(Context context,BillInfo billInfo){
        Caches.update("float_lock","false");
        goUrl(context,billInfo.getQianJi().trim());
    }


    public static void jump(Context context,BillInfo billInfo){
        if(getCheck()){
            showFloat(context,billInfo);
        }else{
            goQianji( context, billInfo);
        }
    }


    public static void run(Context context, BillInfo billInfo) {
        MMKV mmkv = MMKV.defaultMMKV();

        Logs.d(billInfo.toString());
        if(billInfo.getIsSilent()){
            if(mmkv.getBoolean("autoIncome",false)){
                goQianji(context,billInfo);
            }else{
                //通知处理
                Tools.sendNotify(context,"记账提醒","￥"+billInfo.getMoney()+" - "+billInfo.getRemark(),billInfo.getQianJi());
            }
            return;
        }else{
            if(mmkv.getBoolean("autoPay",false)){
                goQianji(context,billInfo);
                return;
            }
        }

        if(getTimeout().equals("0")){
            jump(context,billInfo);
        }else{
            showTip(context,billInfo);
        }

    }
}