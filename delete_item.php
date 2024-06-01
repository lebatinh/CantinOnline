<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$item_id = mysqli_real_escape_string($connect, $_POST['item_id']);

//kiểm tra tham 
if (!empty($item_id)) {

// Sử dụng prepared statements để chèn dữ liệu vào bảng account
$query = "DELETE FROM item WHERE item_id = ?";

// Chuẩn bị câu lệnh SQL
$stmt = mysqli_prepare($connect, $query);

    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
    if ($stmt) {
    // Gán các giá trị vào các tham số của câu lệnh SQL
    mysqli_stmt_bind_param($stmt, "s",$item_id);

    // Thực thi câu lệnh SQL
    if (mysqli_stmt_execute($stmt)) {
        // Thành công
        echo "Xóa vật phẩm thành công!";
    } else {
        // Lỗi khi thực thi câu lệnh SQL
        echo "Xóa vật phẩm thất bại!";
    }

    // Đóng prepared statement
    mysqli_stmt_close($stmt);
} else {
    // Lỗi khi chuẩn bị câu lệnh SQL
    echo "Lỗi xóa vật phẩm!";
}

} else {
    // Xử lý trường hợp khi các tham số rỗng
    echo "Có lỗi xảy ra!";
}
// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
