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
import com.dataenlighten.mj_serious_base.common.bean.MJVehicleObj;
import com.dataenlighten.mj_serious_base.common.bean.Part;
import com.dataenlighten.mj_serious_base.common.bean.QuoteInfo;
import com.dataenlighten.mj_serious_base.exception.LicenseNotFoundException;
import com.dataenlighten.mj_serious_ui.service.MJSDKUIService;
import com.dataenlighten.mj_serious_ui.service.OnSdkUIDamageListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView showText;
    private EditText vin;
    private TextView showOrder;
    private TextView show_partKey;
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
        show_partKey = findViewById(R.id.show_partKey);
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
            public void onDamageSuccess(QuoteInfo quoteInfo) {
                String stringBuilder = "损失详情：" +
                        quoteInfo.toString();
                showOrder.setText(stringBuilder);
//                Intent intent = new Intent();
//                intent.setAction("getSalvageOfferPrice");
//                intent.putExtra("quoteInfo", quoteInfo);
//                startActivity(intent);
            }

            @Override
            public void onDamageFailure(Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                showOrder.setText(e.getMessage());
            }

            @Override
            public void onSelectedKeyParts(ArrayList<Part> partArrayList) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("选择核心配件：");
                for (Part part : partArrayList) {
                    stringBuilder.append(part.getStdPartName()).append(",");
                }
                show_partKey.setText(stringBuilder.toString());
                Toast.makeText(MainActivity.this, "回传核心配件数量==" + partArrayList.size(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
