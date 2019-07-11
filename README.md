## 大案定损SDK简述
本SDK开发旨在提供方便快捷地对大案损失进行金额和配件的确认，通过VIN码进行车辆定型后即可使用圈选。

本SDK使用时需要获取正式授权的license文件。商务合作请联系[明觉科技](http://www.dataenlighten.com)，SDK仅提供合作客户使用，违用必究!

### [点击下载 Demo.apk](https://github.com/Eiffelyk/MJSeriousSDK/blob/master/demo/demo.apk)
### 功能截屏
![avatar](https://github.com/Eiffelyk/MJSeriousSDK/blob/master/demo/Screenshot/demo_%E9%A1%B5%E9%9D%A2.jpg)
![avatar](https://github.com/Eiffelyk/MJSeriousSDK/blob/master/demo/Screenshot/%E5%8A%9F%E8%83%BD_%E5%9C%88%E9%80%89.jpg)
![avatar](https://github.com/Eiffelyk/MJSeriousSDK/blob/master/demo/Screenshot/%E5%8A%9F%E8%83%BD_%E9%80%89%E6%8B%A9%E9%85%8D%E4%BB%B6.jpg)
    
### 更新记录
|时间|版本|更新内容|
|----|-----|-----|
||||

## **使用步骤：**
### 0.拷贝申请到的license.lic（此文件请勿重命名）文件到assets目录中
### 1.添加依赖及权限：
在工程build.gradle配置脚本中buildscript和allprojects段中添加【明觉科技SDK】 新maven仓库地址
```java

allprojects {
    repositories {
        maven {
            url 'https://dl.bintray.com/dataenlighten/MJSeriousSDK'
        }
    }
}
```
#####     *Gradle
 在项目module build.gradle配置脚本中dependencies添加
```java
    //SDK依赖
    implementation 'com.dataenlighten.serious:mj_serious_ui:0.0.4'
    
```

#####     * 在项目AndroidManifest.xml配置脚本中添加权限
```java

    <!-- 网络状态 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!--获取网络状态，请求网络前判断是否有网络-->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  
```
#####     * 在项目AndroidManifest.xml配置脚本中添加权限
```java
    #保留泛型。
    -keepattributes Signature
    #保留异常
    -keepattributes Exceptions
    #保留内部类
    -keepattributes InnerClasses
    #保留枚举
    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }
    
    #-libraryjars libs/okhttp3
    -dontwarn okhttp3.**
    -keep class okhttp3.** { *; }
    
    #-libraryjars libs/okio
    -dontwarn okio.**
    -keep class okio.** { *; }
    #Parcelable
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    
    ##Glide
    -dontwarn com.bumptech.glide.**
    -keep class com.bumptech.glide.**{*;}
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public class * extends com.bumptech.glide.AppGlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
    }
    
    
    # Gson specific classes
    -keep class sun.misc.Unsafe {*;}
    -keep class com.google.gson.stream.** {*;}
    # Application classes that will be serialized/deserialized over Gson
    -keep class com.google.gson.** {*;}
    # 注解
    -keepattributes *Annotation*
    # 反射
    -keepattributes EnclosingMethod
```

## **功能集成：**

###  1.初始化SDK
```java
    /**
     * 服务初始化（非主线程）
     *
     * @param ctx               依赖
     * @param userIdentifier    用户唯一标识
     * @param onSdkInitListener 初始化回调（AbstractSDKInitCallback可切换第三线程和主线程的回调）
     * @throws LicenseNotFoundException 未发现license异常
     */
    void init(Context ctx, String userIdentifier, OnSdkInitListener onSdkInitListener) throws LicenseNotFoundException;
```
###### 示例
````java
try {
            MJSDKUIService.getInstance().init(getApplication(), "userIdentifier", new AbstractSDKInitCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String code, Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (LicenseNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "请检查授权文件", Toast.LENGTH_SHORT).show();
        }
````
###   2.VIN定型
```java
    /**
     * VIN解析，返回对应VIN码的车辆信息
     *
     * @param sessionCode   案件唯一标识，为空或者null的时候，SDK内部生成
     * @param vin           VIN码
     * @param queryCallBack 请求回调。
     */
    void VINQuery(String sessionCode, String vin, QueryCallBack queryCallBack);
```
###### 示例
````java
MJSDKUIService.getInstance().VINQuery("sessionCode", "LFV5A24G1G3000628", new AbstractQueryListBeanCallback<MJVehicleObj>(true) {
            @Override
            public void onFail(String code, Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<MJVehicleObj> resultList) {
            }
        });
````
### 3.初始化案件信息
```java
    /**
     * 初始化赔案信息
     *
     * @param vinCode    VINCode
     * @param carBody    VINQuery返回的CarInfo.getBody()
     * @param optionCode VINQuery返回的CarInfo.getOptionCode()
     */
    void setCarInfo(String vinCode, String carBody, String optionCode);
```
###### 示例
````java
MJSDKUIService.getInstance().setCarInfo(resultList.get(0).getVinCode(), resultList.get(0).getBody(), resultList.get(0).getOptionCode());
````
### 4.开始定损
```java
    /**
     * 开始定损
     *
     * @param ctx                 依赖
     * @param sdkUIDamageListener 回调
     */
    void startDamage(Context ctx, OnSdkUIDamageListener sdkUIDamageListener);
```
###### 示例
````java
MJSDKUIService.getInstance().startDamage(MainActivity.this, new OnSdkUIDamageListener() {
            @Override
            public void onDamageSuccess(PreviewOrderObj previewOrderObj) {
                Toast.makeText(MainActivity.this, previewOrderObj.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDamageFailure(Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
````


### 5.涉及字段说明
#####    MJVehicleObj.java
| 字段名              | 类型    | 说明                                                                      |
| ------------------- | ------ |------------------------------------------------------------------------- |
| mjVehicleSys | String  | 车型（例如：雅阁）                                                       |
| year             | String   | 年款                                                             |
| prefix             | String   | 品牌前缀（Bmw）                                                             |
| gyroBrand             | String   |                                                              |
| carCountry        | String     | 国别         |
| vehicleEn          | String  | 车型英文名         |
| body | String  | 车体信息（三厢4门，MPV，SUV，两厢5门）                                                     |
| vehicleChn          | String  | 车型中文名         |
| vinCode | String  | 提交定型的vin                                                       |
| transmission           | String | 变速器(CVT)                                                        |
| carGrade             | String   | 级别（中大型、微型、中大suv）            |
| subBrand       | String | 子品牌（一汽大众） |
| engine       | String |  |
| optionCode       | String |车型配置编号 |
| optionInfo       | String   | 车型配置信息 |
| displacement        | String     | 排量                                                         |
| mjVehicleGroup       | String   | 车型分组（例如：雅阁8代）|
| priceZone       | String   | |
| drive       | String   | 驱动形式(前轮驱动)|
| brand          | String  | 品牌    |
| minPrice | String  | 最低价格                           |
| maxPrice       | String   | 最高价格|

##### MJPartObj.java
| 字段名              | 类型    | 说明                                                                      |
| ------------------- | ------ |------------------------------------------------------------------------- |
| thumbnailURl | String  | 缩略图URL                                                      |
| stdPartName       | String   | 标准名|
| srcPartName       | String | 原厂名 |
| partNumber           | String | OE号                                                  |
| partPrice        | String     | 4s店价格                                                   |
| collisionCodes| List     | 该配件所属碰撞区域编码 “T00100”                                      |

##### MJLaborObj
| 字段名              | 类型    | 说明                                                                      |
| ------------------- | ------ |------------------------------------------------------------------------- |
| laborName       | String   |工时名称-例如  前保险杠皮|
| operation       | String |与中台交互的实际工项（例如: replace、panel、fit、accessoryFit、paint, material、externalRepair、electrical）【更换、钣金/维修、拆装、附件拆装、喷漆、辅料、外修、机电】 |
| laborHour           | String | 工时数量                                                  |
| laborCost        | String     | 工时价格                                                   |


### 6.其他错误码表

| 错误码 | 描述                   |
| ------ | ------------------ |
| 0000   | 成功                    |
| 1001   | VIN不合法               |
| 1002   | VIN不支持               |
| 1003   | 非乘用车                |
| 1004   | VIN错误                 |
| 1005   | VIN无法解析             |
| 1006   | 无权查看该品牌数据      |
| 1011   | 请求VIN的配件数据不存在 |
| 1012   | 该车无此配件            |
| 1013   | 请求图片不存在          |
| 1014   | 推荐配件数据不存在      |
| 1015   | 该车型查不到配件        |
| 9007   | 后台API异常             |
|8000002|	系统验证：成功退出系统！|
|0000|	系统验证：成功刷新访问票据！|
|8000004|	系统验证: 账号不存在或无权限!|
|8000005|	系统验证：需要进一步鉴权.|
|9999999|	其他错误，请联系技术支持！|
|9000001|	系统验证：请求参数不完整或者有错误！|
|9002|    系统验证：访问票据非法或已过期！|
|9000004|	系统验证：权限不足，无权访问该产品.|
|9000005|	系统验证：权限不足，无权访问该资源.|
|9006|	系统验证：认证失败！|
|9000007|	系统验证：请求验证码过于频繁 请稍后重试.|
|9000008|	系统验证：访问过于频繁 请稍后重试.|
|9000009|	系统验证：权限不足.|
|9000010|	服务响应超时 请稍后重试.|
|9000011|	请输入用户组名称|
|9000012|	外键错误|
|9000013|	手机号码格式错误|
|9000014|	列名部分有问题，请再次确认|
|9000015|	手机号已注册|
|9000024|	用户不存在|
|9000016|	用户名已注册|
|9000017|	分组已存在|
|9000018|	分组公司关系已存在|
|9000019|	请输入分组ID|
|9000020|	请选择分组|
|9000021|	请选择公司|
|9000022|	组织信息未填写，请确认|
|9005|	服务器升级中!|

