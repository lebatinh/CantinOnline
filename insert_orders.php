<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
$receiver_name = mysqli_real_escape_string($connect, $_POST['receiver_name']);
$phonenumber = mysqli_real_escape_string($connect, $_POST['phonenumber']);
$order_content = mysqli_real_escape_string($connect, $_POST['order_content']);
$total_payment_amount = mysqli_real_escape_string($connect, $_POST['total_payment_amount']);
$order_time = mysqli_real_escape_string($connect, $_POST['order_time']);
$note = isset($_POST['note']) ? mysqli_real_escape_string($connect, $_POST['note']) : '';
$payment_method = mysqli_real_escape_string($connect, $_POST['payment_method']);
$order_status = mysqli_real_escape_string($connect, $_POST['order_status']);

// Tạo order_id dựa trên thời gian hiện tại và số thứ tự cuối cùng trong bảng orders
$current_time = date("Ymd");
$query_last_order = "SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1";
$result_last_order = mysqli_query($connect, $query_last_order);
if ($result_last_order && mysqli_num_rows($result_last_order) > 0) {
    $row_last_order = mysqli_fetch_assoc($result_last_order);
    $last_order_id = (int)substr($row_last_order['order_id'], 8) + 1;
} else {
    $last_order_id = 0;
}
$order_id = $current_time . str_pad($last_order_id, 4, '0', STR_PAD_LEFT);

// Khởi tạo mảng phản hồi
$response = array();

if ($payment_method == "Tiền tài khoản") {
    $query_balance = "SELECT sodu FROM finance WHERE acc_id = '$acc_id'";
    $result_balance = mysqli_query($connect, $query_balance);
    if ($result_balance && mysqli_num_rows($result_balance) > 0) {
        $row_balance = mysqli_fetch_assoc($result_balance);
        $sodu = $row_balance['sodu'];
        if ($sodu >= $total_payment_amount) {
            // Trừ số dư
            $new_balance = $sodu - $total_payment_amount;
            $query_update_balance = "UPDATE finance SET sodu = $new_balance WHERE acc_id = '$acc_id'";
            mysqli_query($connect, $query_update_balance);

            // Cập nhật số dư cho admin
            $query_admin_balance = "SELECT sodu FROM finance WHERE acc_id = 'admin'";
            $result_admin_balance = mysqli_query($connect, $query_admin_balance);
            if ($result_admin_balance && mysqli_num_rows($result_admin_balance) > 0) {
                $row_admin_balance = mysqli_fetch_assoc($result_admin_balance);
                $admin_sodu = $row_admin_balance['sodu'] + $total_payment_amount;
                $query_update_admin_balance = "UPDATE finance SET sodu = $admin_sodu WHERE acc_id = 'admin'";
                mysqli_query($connect, $query_update_admin_balance);
            } else {
                $response['message'] = "Không tìm thấy tài khoản admin!";
                echo json_encode($response);
                mysqli_close($connect);
                exit();
            }


            $pay_time = mysqli_real_escape_string($connect, $_POST['pay_time']);

            // Thêm vào bảng balance_change
            $query_balance_change = "INSERT INTO balance_change (acc_id, money_transaction, receiver, time_transaction, type, descriptions, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            $stmt_balance_change = mysqli_prepare($connect, $query_balance_change);
            if ($stmt_balance_change) {
                $receiver = "admin";
                $type = "Thanh toán hóa đơn";
                $descriptions = "Tiền tài khoản";
                $status = "Hoàn thành";
                mysqli_stmt_bind_param($stmt_balance_change, "sssssss", $acc_id, $total_payment_amount, $receiver, $pay_time, $type, $descriptions, $status);
                mysqli_stmt_execute($stmt_balance_change);
                mysqli_stmt_close($stmt_balance_change);
            } else {
                $response['message'] = "Lỗi khi chuẩn bị câu lệnh SQL cho bảng balance_change!";
                echo json_encode($response);
                mysqli_close($connect);
                exit();
            }

            // Chèn đơn hàng mới
            $query_insert = "INSERT INTO orders (order_id, acc_id, receiver_name, phonenumber, order_content, total_payment_amount, order_time, note, payment_method, order_status, pay_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            $stmt = mysqli_prepare($connect, $query_insert);
            if ($stmt) {
                mysqli_stmt_bind_param($stmt, "sssssssssss", $order_id, $acc_id, $receiver_name, $phonenumber, $order_content, $total_payment_amount, $order_time, $note, $payment_method, $order_status, $pay_time);
                if (mysqli_stmt_execute($stmt)) {
                    $response['message'] = "Thanh toán thành công";
                    $response['data'] = array(
                        "order_id" => $order_id
                    );
                } else {
                    $response['message'] = "Lỗi khi chèn dữ liệu!";
                }
                mysqli_stmt_close($stmt);
            } else {
                $response['message'] = "Lỗi khi chuẩn bị câu lệnh SQL!";
            }
        } else {
            $response['message'] = "Bạn không đủ tiền thanh toán hóa đơn này!";
        }
    } else {
        $response['message'] = "Không tìm thấy tài khoản!";
    }
} else if ($payment_method == "Tiền mặt") {
    $query_insert = "INSERT INTO orders (order_id, acc_id, receiver_name, phonenumber, order_content, total_payment_amount, order_time, note, payment_method, order_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = mysqli_prepare($connect, $query_insert);
    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "ssssssssss", $order_id, $acc_id, $receiver_name, $phonenumber, $order_content, $total_payment_amount, $order_time, $note, $payment_method, $order_status);
        if (mysqli_stmt_execute($stmt)) {
            $response['message'] = "Thanh toán thành công";
            $response['data'] = array(
                "order_id" => $order_id
            );
        } else {
            $response['message'] = "Lỗi khi chèn dữ liệu!";
        }
        mysqli_stmt_close($stmt);
    } else {
        $response['message'] = "Lỗi khi chuẩn bị câu lệnh SQL!";
    }
}

echo json_encode($response);
mysqli_close($connect);
?>
