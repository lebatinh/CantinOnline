<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST tồn tại
if (!empty($_POST['acc_id']) && !empty($_POST['sotien']) && !empty($_POST['history']) && !empty($_POST['type'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
    $transaction_id = mysqli_real_escape_string($connect, $_POST['transaction_id']);
    $sotien = mysqli_real_escape_string($connect, $_POST['sotien']);
    $history = mysqli_real_escape_string($connect, $_POST['history']);
    $type = mysqli_real_escape_string($connect, $_POST['type']);
    $status = isset($_POST['status']) ? mysqli_real_escape_string($connect, $_POST['status']) : "chờ duyệt";
    $loinhan = mysqli_real_escape_string($connect, $_POST['loinhan']);

    // Định dạng thời gian
    $datetime_format = 'STR_TO_DATE(?, "%H:%i:%s %d/%m/%Y")';

    // Kiểm tra xem đã có giao dịch nạp tiền trong vòng 5 phút trước đó chưa
    $check_query = "SELECT * FROM request_change_money WHERE acc_id = ? AND STR_TO_DATE(history, '%H:%i:%s %d/%m/%Y') >= DATE_SUB(NOW(), INTERVAL 5 MINUTE)";
    if ($check_stmt = mysqli_prepare($connect, $check_query)) {
        mysqli_stmt_bind_param($check_stmt, "s", $acc_id);
        mysqli_stmt_execute($check_stmt);
        mysqli_stmt_store_result($check_stmt);

        if (mysqli_stmt_num_rows($check_stmt) > 0) {
            // Đã có yêu cầu nạp tiền trong vòng 5 phút trước đó
            echo json_encode(array("message" => "Bạn phải chờ 5 phút trước khi tạo yêu cầu mới!"));
        } else {
            // Chưa có yêu cầu trong 5 phút gần đây, tiếp tục thêm yêu cầu mới
            $query = "INSERT INTO request_change_money (acc_id, transaction_id, sotien, history, type, status, loinhan) VALUES (?, ?, ?, ?, ?, ?, ?)";

            if ($stmt = mysqli_prepare($connect, $query)) {
                mysqli_stmt_bind_param($stmt, "ssissss", $acc_id, $transaction_id, $sotien, $history, $type, $status, $loinhan);

                if (mysqli_stmt_execute($stmt)) {
                    echo json_encode(array("message" => "Tạo yêu cầu thành công!"));
                } else {
                    echo json_encode(array("message" => "Lỗi: " . mysqli_stmt_error($stmt)));
                }

                mysqli_stmt_close($stmt);
            } else {
                echo json_encode(array("message" => "Lỗi: " . mysqli_error($connect)));
            }
        }

        mysqli_stmt_close($check_stmt);
    } else {
        echo json_encode(array("message" => "Lỗi: " . mysqli_error($connect)));
    }
} else {
    echo json_encode(array("message" => "Tham số rỗng!"));
}

mysqli_close($connect);
?>
