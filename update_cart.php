<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
$item_id = mysqli_real_escape_string($connect, $_POST['item_id']);
$quantity = mysqli_real_escape_string($connect, $_POST['quantity']);

// Sử dụng prepared statements để cập nhật dữ liệu trong bảng cart
$query = "UPDATE cart SET quantity = ? WHERE acc_id = ? AND item_id = ?";

// Chuẩn bị câu lệnh SQL
$stmt = mysqli_prepare($connect, $query);

if (!empty($acc_id) && !empty($item_id)) {
    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
if ($stmt) {
    // Gán các giá trị vào các tham số của câu lệnh SQL
    mysqli_stmt_bind_param($stmt, "iss", $quantity, $acc_id, $item_id);

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
