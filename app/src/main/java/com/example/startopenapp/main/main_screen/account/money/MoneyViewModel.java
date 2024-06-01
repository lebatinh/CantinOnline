package com.example.startopenapp.main.main_screen.account.money;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.startopenapp.display_manager.TimeHelper;
import com.example.startopenapp.retrofit.ConnectToServer;
import com.example.startopenapp.retrofit.RetrofitManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MoneyViewModel extends ViewModel {
    RetrofitManager retrofitManagerMM;
    private MutableLiveData<String> stdId = new MutableLiveData<>();
    private MutableLiveData<String> transaction_id = new MutableLiveData<>();
    private MutableLiveData<String> sotiennap = new MutableLiveData<>();
    private MutableLiveData<String> noidungnap = new MutableLiveData<>();
    private final MutableLiveData<Money> money = new MutableLiveData<>();
    private final MutableLiveData<Info> info = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> sodu = new MutableLiveData<>();
    private MutableLiveData<String> status = new MutableLiveData<>();
    private MutableLiveData<String> tienrut = new MutableLiveData<>();
    private MutableLiveData<String> bank = new MutableLiveData<>();
    private MutableLiveData<String> stk = new MutableLiveData<>();
    private MutableLiveData<String> ctk = new MutableLiveData<>();
    private long lastRequestTime = 0;

    public MutableLiveData<Money> getMoney() {
        return money;
    }

    public MutableLiveData<Info> getInfo() {
        return info;
    }

    public MutableLiveData<String> getStdId() {
        return stdId;
    }

    public void setStdId(MutableLiveData<String> stdId) {
        this.stdId = stdId;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }

    public MutableLiveData<String> getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(MutableLiveData<String> transaction_id) {
        this.transaction_id = transaction_id;
    }

    public MutableLiveData<String> getSodu() {
        return sodu;
    }

    public void setSodu(MutableLiveData<String> sodu) {
        this.sodu = sodu;
    }

    public MutableLiveData<String> getSotiennap() {
        return sotiennap;
    }

    public void setSotiennap(MutableLiveData<String> sotiennap) {
        this.sotiennap = sotiennap;
    }

    public MutableLiveData<String> getNoidungnap() {
        return noidungnap;
    }

    public void setNoidungnap(MutableLiveData<String> noidungnap) {
        this.noidungnap = noidungnap;
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void setStatus(MutableLiveData<String> status) {
        this.status = status;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<String> getTienrut() {
        return tienrut;
    }

    public void setTienrut(MutableLiveData<String> tienrut) {
        this.tienrut = tienrut;
    }

    public MutableLiveData<String> getBank() {
        return bank;
    }

    public void setBank(MutableLiveData<String> bank) {
        this.bank = bank;
    }

    public MutableLiveData<String> getStk() {
        return stk;
    }

    public void setStk(MutableLiveData<String> stk) {
        this.stk = stk;
    }

    public MutableLiveData<String> getCtk() {
        return ctk;
    }

    public void setCtk(MutableLiveData<String> ctk) {
        this.ctk = ctk;
    }

    public void getInforAcc(String accId) {
        // Gửi dữ liệu lên server
        Map<String, String> dataToSend = new HashMap<>();
        dataToSend.put("acc_id", accId);

        String phpFilePath = ConnectToServer.getSelect_money_phpFilePath();
        String url = ConnectToServer.getSelect_money_url();

        retrofitManagerMM = new RetrofitManager(url);
        retrofitManagerMM.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.has("message")) {
                        String msg = responseObject.getString("message");
                        message.postValue(msg);
                    } else {
                        JSONArray dataArray = responseObject.getJSONArray("data");
                        JSONObject dataObject = dataArray.getJSONObject(0);

                        // Dữ liệu hợp lệ, lấy giá trị từ dataObject
                        String name = dataObject.getString("name");
                        String sodu = dataObject.optString("sodu", "0"); // Sử dụng optString để có giá trị mặc định là "0"

                        // Kiểm tra nếu sodu là rỗng hoặc null thì đặt giá trị mặc định là "0"
                        if (sodu.equals("null") || sodu.isEmpty()) {
                            sodu = "0";
                        }
                        Money money = new Money(getStdId().getValue(), name, sodu);
                        MoneyViewModel.this.money.postValue(money);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    message.postValue("Error parsing response!");
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                message.postValue("Error sending data to server!");
                Log.d("error", error);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickSendYcNap(Context context) {
        String id = getStdId().getValue();
        String sotien = getSotiennap().getValue();
        String loinhan = "nap tien tk " + id;
        String history = TimeHelper.getFormattedTime();
        if (sotien == null || sotien.isEmpty()) {
            message.postValue("Bạn chưa nhập số tiền muốn nạp!");
        } else {
            // Tạo magiaodich theo cú pháp: năm tháng ngày giờ phút + stdId
            String magiaodich = generateTransactionId(id);
            transaction_id.postValue(magiaodich);
            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("acc_id", id);
            dataToSend.put("transaction_id", magiaodich);
            dataToSend.put("sotien", sotien);
            dataToSend.put("history", history);
            dataToSend.put("type", "nạp tiền");
            dataToSend.put("loinhan", loinhan);

            yeuCauNapTien(dataToSend, context);
        }
    }

    public void yeuCauNapTien(Map<String, String> dataToSend, Context context) {
        // Kiểm tra thời gian yêu cầu cuối cùng
        if (!isTimeElapsed()) {
            // Nếu chưa đủ thời gian, thông báo cho người dùng và không gửi yêu cầu
            message.postValue("You must wait 5 minutes before making a new request.");
            return;
        }

        String phpFilePath = ConnectToServer.getInsert_request_change_money_phpFilePath();
        String url = ConnectToServer.getInsert_request_change_money_url();

        retrofitManagerMM = new RetrofitManager(url);
        retrofitManagerMM.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    String msg = responseObject.getString("message");

                    message.postValue(msg);

                    if (msg.equals("Tạo yêu cầu thành công!")) {
                        changeToInfoRecharge(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("checkReplace", "JSON parsing error");
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                message.postValue("Error sending data to server!");
                Log.d("error1", error);
            }
        });
    }

    private void changeToInfoRecharge(Context context) {
        Intent intent = new Intent(context, InfoRecharge.class);
        intent.putExtra("acc_id", getStdId().getValue());
        intent.putExtra("transaction_id", getTransaction_id().getValue());
        intent.putExtra("type", "nạp tiền");
        context.startActivity(intent);
    }

    public void huyNapTien(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getUpdate_money_balance_phpFilePath();
        String url = ConnectToServer.getUpdate_money_balance_url();

        retrofitManagerMM = new RetrofitManager(url);
        retrofitManagerMM.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                // Kiểm tra xem response có chứa thông báo thành công không
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.has("message")) {
                        String msg = responseObject.getString("message");
                        message.postValue(msg);
                    } else {
                        // Xử lý phản hồi không hợp lệ từ server
                        message.postValue("Invalid response from server");
                    }
                } catch (JSONException e) {
                    // Xử lý lỗi khi parse JSON
                    message.postValue("Error parsing response from server");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                message.postValue("Error sending data to server!");
                Log.d("error2", error);
            }
        });
    }

    private boolean isTimeElapsed() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRequestTime;

        if (elapsedTime >= 5 * 60 * 1000) {
            lastRequestTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String generateTransactionId(String accId) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String formattedDateTime = currentTime.format(formatter);

        return formattedDateTime + accId;
    }

    public void getDataRechargeMoney(Map<String, String> dataToSend) {
        String phpFilePath = ConnectToServer.getSelect_recharge_money_phpFilePath();
        String url = ConnectToServer.getSelect_recharge_money_url();

        retrofitManagerMM = new RetrofitManager(url);
        retrofitManagerMM.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("responseVM", response);
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.has("message")) {
                        String msg = responseObject.getString("message");
                        message.postValue(msg);
                    } else {
                        // Dữ liệu hợp lệ
                        JSONArray jsonArray = responseObject.getJSONArray("data");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String sotien = jsonObject.getString("sotien");
                        String status = jsonObject.getString("status");
                        String history = jsonObject.getString("history");
                        String loinhan = jsonObject.getString("loinhan");

                        Info info = new Info(sotien, status, history, loinhan);
                        MoneyViewModel.this.info.postValue(info);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                message.postValue("Error sending data to server!");
                Log.d("error1", error);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickSendYcRut() {
        String id = getStdId().getValue();
        String sotienrut = getTienrut().getValue();
        String loinhan = "Ngân hàng: " +getBank().getValue() +" - "+ "STK: " +getStk().getValue() +" - "+ "CTK: " +getCtk().getValue();
        String history = TimeHelper.getFormattedTime();
        if (sotienrut == null || sotienrut.isEmpty()) {
            message.postValue("Bạn chưa nhập số tiền muốn nạp!");
        } else if ((Integer.parseInt(sotienrut) > Integer.parseInt(money.getValue().getSodu().toString()))) {
            message.postValue("Bạn không thể rút quá số tiền đang có!");
        } else {
            // Tạo magiaodich theo cú pháp: năm tháng ngày giờ phút + stdId
            String magiaodich = generateTransactionId(id);
            transaction_id.postValue(magiaodich);
            // Gửi dữ liệu lên server
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("acc_id", id);
            dataToSend.put("transaction_id", magiaodich);
            dataToSend.put("sotien", sotienrut);
            dataToSend.put("history", history);
            dataToSend.put("type", "rút tiền");
            dataToSend.put("loinhan", loinhan);

            yeuCauRutTien(dataToSend);
        }
    }

    public void yeuCauRutTien(Map<String, String> dataToSend) {
        // Kiểm tra thời gian yêu cầu cuối cùng
        if (!isTimeElapsed()) {
            // Nếu chưa đủ thời gian, thông báo cho người dùng và không gửi yêu cầu
            message.postValue("Bạn phải chờ 5 phút trước khi tạo yêu cầu mới!");
            return;
        }

        String phpFilePath = ConnectToServer.getInsert_request_change_money_phpFilePath();
        String url = ConnectToServer.getInsert_request_change_money_url();

        retrofitManagerMM = new RetrofitManager(url);
        retrofitManagerMM.sendDataToServer(url, phpFilePath, dataToSend, new RetrofitManager.DataCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    String msg = responseObject.getString("message");

                    message.postValue(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("checkReplace", "JSON parsing error");
                }
            }

            @Override
            public void onError(String error) {
                // Xử lý lỗi
                message.postValue("Error sending data to server!");
                Log.d("error1", error);
            }
        });
    }

    public void recharge(Map<String, String> data){
        String phpFilePathNoti = ConnectToServer.getUpdate_money_balance_phpFilePath();
        String urlMoney = ConnectToServer.getUpdate_money_balance_url();

        // Khởi tạo RetrofitManager với baseUrl
        retrofitManagerMM = new RetrofitManager(urlMoney);
        retrofitManagerMM.sendDataToServer(urlMoney, phpFilePathNoti, data, new RetrofitManager
                .DataCallback() {

            @Override
            public void onSuccess(String response) {
                Log.d("response", response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    // Kiểm tra xem dữ liệu có chứa thông báo lỗi không
                    if (responseObject.has("message")) {
                        String errorMessage = responseObject.getString("message");
                        // thông báo lỗi
                        message.postValue(errorMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.postValue("Lỗi phân tích dữ liệu!");
                }
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
                message.postValue("Lỗi gửi yêu cầu cho máy chủ!");
            }
        });
    }
}
