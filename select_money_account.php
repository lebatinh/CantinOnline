<?php
require "DBConnect.php";

$data = array();

if (isset($_POST['acc_id'])) {
    $acc_id = $_POST['acc_id'];

// Truy vấn để lấy dữ liệu từ bảng account
    $sql_account = "SELECT name FROM account WHERE acc_id = ?";
    if ($stmt_account = $connect->prepare($sql_account)) {
        $stmt_account->bind_param("s", $acc_id);
        $stmt_account->execute();
        $result_account = $stmt_account->get_result();

        if ($result_account->num_rows > 0) {
            $accountData = $result_account->fetch_assoc();
            $data['name'] = $accountData['name'];
        } else {
            $data['name'] = null;
        }

        $stmt_account->close();
    } else {
        echo json_encode(array('message' => 'Lỗi không xác định được thông tin tài khoản'));
        exit;
    }
    
    // Truy vấn để lấy dữ liệu từ bảng finance
    $sql_info_user = "SELECT sodu FROM finance WHERE acc_id = ?";
    if ($stmt_info_user = $connect->prepare($sql_info_user)) {
        $stmt_info_user->bind_param("s", $acc_id);
        $stmt_info_user->execute();
        $result_info_user = $stmt_info_user->get_result();

        if ($result_info_user->num_rows > 0) {
            $data['sodu'] = $result_info_user->fetch_assoc()['sodu'];
        } else {
            $data['sodu'] = null;
        }

        $stmt_info_user->close();
    } else {
        echo json_encode(array('message' => 'Lỗi không thấy số dư tài khoản'));
        exit;
    }

    // Trả về dữ liệu dưới dạng JSON
    echo json_encode(array('data' => array($data)));

} else {
    echo json_encode(array('message' => 'Thiếu thông tin acc_id'));
}

mysqli_close($connect);
?>
