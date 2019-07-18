package com.dataenlighten.mjserioussdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dataenlighten.mj_serious_base.callback.AbstractQueryListBeanCallback;
import com.dataenlighten.mj_serious_base.callback.AbstractSDKInitCallback;
import com.dataenlighten.mj_serious_base.common.bean.MJLaborObj;
import com.dataenlighten.mj_serious_base.common.bean.MJPartObj;
import com.dataenlighten.mj_serious_base.common.bean.MJVehicleObj;
import com.dataenlighten.mj_serious_base.common.bean.PreviewOrderObj;
import com.dataenlighten.mj_serious_base.exception.LicenseNotFoundException;
import com.dataenlighten.mj_serious_ui.service.MJSDKUIService;
import com.dataenlighten.mj_serious_ui.service.OnSdkUIDamageListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView showText;
    private EditText vin;
    private TextView showOrder;
    private Button authentic;
    private Button button;
    private Button damage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        showText = findViewById(R.id.show);
        vin = findViewById(R.id.vin);
        showOrder = findViewById(R.id.show_order);
        authentic = findViewById(R.id.authentic);
        button = findViewById(R.id.button);
        damage = findViewById(R.id.damage);
    }

    public void click(View view) {
        initSDK();
    }

    /**
     * 初始化SDK
     */
    private void initSDK() {
        try {
            MJSDKUIService.getInstance().init(getApplication(), "userIdentifier", new AbstractSDKInitCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    damage.setEnabled(false);
                }

                @Override
                public void onFail(String code, Exception e) {
                    button.setEnabled(false);
                    damage.setEnabled(false);
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (LicenseNotFoundException e) {
            e.printStackTrace();
            button.setEnabled(false);
            damage.setEnabled(false);
            Toast.makeText(this, "请检查授权文件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * vin定型
     */
    public void vinQuery(View view) {
        MJSDKUIService.getInstance().VINQuery("sessionCode" + System.currentTimeMillis(), vin.getText().toString(), new AbstractQueryListBeanCallback<MJVehicleObj>(true) {
            @Override
            public void onFail(String code, Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                showText.setText(e.getMessage());
                damage.setEnabled(false);
            }

            @Override
            public void onSuccess(List<MJVehicleObj> resultList) {
                if (resultList != null && resultList.size() > 0) {
                    damage.setEnabled(true);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("定型结果：").append(resultList.size()).append("辆\n");
                    for (MJVehicleObj mjVehicleObj : resultList) {
                        stringBuilder.append("车辆信息：").append(mjVehicleObj.getYear()).append("款 ").append(mjVehicleObj.getSubBrand()).append(" ").append(mjVehicleObj.getMjVehicleSys()).append(" ").append(mjVehicleObj.getDisplacement()).append(" ").append(mjVehicleObj.getTransmission());
                    }
                    showText.setText(stringBuilder.toString());
                    initCarInfo(resultList.get(0));
                } else {
                    damage.setEnabled(false);
                    Toast.makeText(MainActivity.this, "未锁定车型", Toast.LENGTH_SHORT).show();
                    showText.setText("未锁定车型");
                }
            }
        });
    }


    /**
     * 初始化车辆信息
     */
    private void initCarInfo(MJVehicleObj mjVehicleObj) {
        MJSDKUIService.getInstance().setCarInfo(mjVehicleObj.getVinCode(), mjVehicleObj.getBody(), mjVehicleObj.getOptionCode());
    }

    /**
     * 开始定损
     */
    public void startDamage(View view) {
        MJSDKUIService.getInstance().startDamage(MainActivity.this, new OnSdkUIDamageListener() {
            @Override
            public void onDamageSuccess(PreviewOrderObj previewOrderObj) {
                double totalPrice = 0.00;
                StringBuilder stringBuilder = new StringBuilder();
                ArrayList<MJPartObj> partObjs = previewOrderObj.getPartList();
                if (partObjs != null && partObjs.size() > 0) {
                    double totalPriceByPart = 0.00;
                    for (int i = 0; i < partObjs.size(); i++) {
                        totalPriceByPart += Double.parseDouble(partObjs.get(i).getPartPrice());
                    }
                    totalPrice += totalPriceByPart;
                    stringBuilder.append("配件金额：").append(new DecimalFormat("0.00").format(totalPriceByPart)).append("元\n");
                }
                ArrayList<MJLaborObj> mjLaborObjs = previewOrderObj.getLaborList();
                if (mjLaborObjs != null && mjLaborObjs.size() > 0) {
                    double totalPriceByLabor = 0.00;
                    for (int i = 0; i < mjLaborObjs.size(); i++) {
                        totalPriceByLabor += Double.parseDouble(mjLaborObjs.get(i).getLaborCost());
                    }
                    totalPrice += totalPriceByLabor;
                    stringBuilder.append("工时金额：").append(new DecimalFormat("0.00").format(totalPriceByLabor)).append("元\n");
                }
                stringBuilder.append("定损评估：").append(new DecimalFormat("0.00").format(totalPrice)).append("元\n");
                stringBuilder.append("定损明细：").append(previewOrderObj.toString());
                showOrder.setText(stringBuilder.toString());
            }

            @Override
            public void onDamageFailure(Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                showOrder.setText(e.getMessage());
            }
        });
    }
}
