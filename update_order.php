<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$order_id = mysqli_real_escape_string($connect, $_POST['order_id']);
$order_status = mysqli_real_escape_string($connect, $_POST['order_status']);
$complete_time = mysqli_real_escape_string($connect, $_POST['complete_time']);

// Sử dụng prepared statements để cập nhật dữ liệu trong bảng cart
$query = "UPDATE orders SET complete_time = ?, order_status = ? WHERE order_id = ? AND order_status = "Đặt đơn";

// Chuẩn bị câu lệnh SQL
$stmt = mysqli_prepare($connect, $query);

if (!empty($order_id) && !empty($order_status) && !empty($complete_time)) {
    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
if ($stmt) {
    // Gán các giá trị vào các tham số của câu lệnh SQL
    mysqli_stmt_bind_param($stmt, "sss", $complete_time, $order_status, $order_id);

    // Thực thi câu lệnh SQL
    if (mysqli_stmt_execute($stmt)) {
        // Thành công
        echo "success";
    } else {
        // Lỗi khi thực thi câu lệnh SQL
        echo "fail";
    }

    // Đóng prepared statement
    mysqli_stmt_close($stmt);
} else {
    // Lỗi khi chuẩn bị câu lệnh SQL
    echo "error";
}
}else{
    echo "null";
}


// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
