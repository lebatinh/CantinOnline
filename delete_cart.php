<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
$item_id = mysqli_real_escape_string($connect, $_POST['item_id']);

// Kiểm tra nếu item_id là "all"
if ($item_id === "all") {
    // Xóa tất cả dữ liệu cho acc_id đã chỉ định
    $query = "DELETE FROM cart WHERE acc_id = ?";
    $stmt = mysqli_prepare($connect, $query);
    mysqli_stmt_bind_param($stmt, "s", $acc_id);
} else {
    // Xóa dữ liệu cho acc_id và item_id đã chỉ định
    $query = "DELETE FROM cart WHERE acc_id = ? AND item_id = ?";
    $stmt = mysqli_prepare($connect, $query);
    mysqli_stmt_bind_param($stmt, "ss", $acc_id, $item_id);
}

// Thực thi câu lệnh SQL
if ($stmt && mysqli_stmt_execute($stmt)) {
    // Thành công
    echo "Xóa thành công";
} else {
    // Lỗi
    echo "Xóa thất bại";
}

// Đóng prepared statement và kết nối
mysqli_stmt_close($stmt);
mysqli_close($connect);
?>
