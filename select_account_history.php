<?php
require "DBConnect.php";

if (isset($_POST['acc_id'])) {
    $acc_id = $_POST['acc_id'];

    // Truy vấn để lấy ngày của 'Đăng ký tài khoản' cuối cùng
    $sqlSignIn = "SELECT history FROM account_history WHERE acc_id = ? AND type = 'Đăng ký tài khoản' ORDER BY history DESC LIMIT 1";
    $stmtSignIn = $connect->prepare($sqlSignIn);
    $stmtSignIn->bind_param("s", $acc_id);
    $stmtSignIn->execute();
    $resultSignIn = $stmtSignIn->get_result();
    $signInDate = $resultSignIn->num_rows > 0 ? $resultSignIn->fetch_assoc()['history'] : null;

    // Truy vấn để lấy ngày ở vị trí thứ 2 từ dưới lên của 'Đăng nhập'
    $sqlSecondLastLogin = "SELECT history FROM account_history WHERE acc_id = ? AND type = 'Đăng nhập' ORDER BY history DESC LIMIT 1 OFFSET 1";
    $stmtSecondLastLogin = $connect->prepare($sqlSecondLastLogin);
    $stmtSecondLastLogin->bind_param("s", $acc_id);
    $stmtSecondLastLogin->execute();
    $resultSecondLastLogin = $stmtSecondLastLogin->get_result();
    $secondLastLoginDate = $resultSecondLastLogin->num_rows > 0 ? $resultSecondLastLogin->fetch_assoc()['history'] : null;

    // Truy vấn để lấy ngày của 'Đổi mật khẩu' cuối cùng
    $sqlChangePassword = "SELECT history FROM account_history WHERE acc_id = ? AND type = 'Đổi mật khẩu' ORDER BY history DESC LIMIT 1";
    $stmtChangePassword = $connect->prepare($sqlChangePassword);
    $stmtChangePassword->bind_param("s", $acc_id);
    $stmtChangePassword->execute();
    $resultChangePassword = $stmtChangePassword->get_result();
    $changePasswordDate = $resultChangePassword->num_rows > 0 ? $resultChangePassword->fetch_assoc()['history'] : null;

    // Truy vấn để lấy ngày của 'Xác minh tài khoản' cuối cùng
    $sqlVerifyAccount = "SELECT history FROM account_history WHERE acc_id = ? AND type = 'Xác minh tài khoản' ORDER BY history DESC LIMIT 1";
    $stmtVerifyAccount = $connect->prepare($sqlVerifyAccount);
    $stmtVerifyAccount->bind_param("s", $acc_id);
    $stmtVerifyAccount->execute();
    $resultVerifyAccount = $stmtVerifyAccount->get_result();
    $verifyAccountDate = $resultVerifyAccount->num_rows > 0 ? $resultVerifyAccount->fetch_assoc()['history'] : null;

    // Đóng kết nối
    $stmtSignIn->close();
    $stmtSecondLastLogin->close();
    $stmtChangePassword->close();
    $stmtVerifyAccount->close();
    $connect->close();

    // Tạo mảng kết quả với các thông báo mượt mà hơn
    $response = array(
        'signInDate' => $signInDate ? "Bạn đã đăng ký tài khoản vào lúc: $signInDate" : 'Thông tin đăng ký tài khoản không có sẵn!',
        'secondLastLoginDate' => $secondLastLoginDate ? "Lần đăng nhập gần nhất trước đó của bạn vào lúc: $secondLastLoginDate" : 'Bạn mới đăng nhập lần đầu!',
        'changePasswordDate' => $changePasswordDate ? "Bạn đã đổi mật khẩu lần cuối vào lúc: $changePasswordDate" : 'Bạn chưa từng đổi mật khẩu!',
        'verifyAccountDate' => $verifyAccountDate ? "Bạn đã xác minh tài khoản vào lúc: $verifyAccountDate" : 'Bạn chưa xác minh tài khoản!'
    );

    // Trả về dữ liệu dưới dạng JSON
    echo json_encode($response);
} else {
    // Trường hợp acc_id không được gửi lên
    echo json_encode(array('message' => 'Thiếu thông tin'));
}
?>
