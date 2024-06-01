<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$product_type = mysqli_real_escape_string($connect, $_POST['product_type']);
$item_id = mysqli_real_escape_string($connect, $_POST['item_id']);
$item_name = mysqli_real_escape_string($connect, $_POST['item_name']);
$hinhanh = mysqli_real_escape_string($connect, $_POST['hinhanh']);
$introduce = mysqli_real_escape_string($connect, $_POST['introduce']);
$price = mysqli_real_escape_string($connect, $_POST['price']);

// Sử dụng prepared statements để cập nhật dữ liệu trong bảng item
$query = "UPDATE item SET product_type = ?, item_name = ?, hinhanh = ?, introduce = ?, price = ? WHERE item_id = ?";

// Chuẩn bị câu lệnh SQL
$stmt = mysqli_prepare($connect, $query);

if (!empty($product_type) && !empty($item_id) && !empty($item_name) && !empty($hinhanh) && !empty($introduce) && !empty($price)) {
    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
    if ($stmt) {
        // Gán các giá trị vào các tham số của câu lệnh SQL
        mysqli_stmt_bind_param($stmt, "sssssi", $product_type, $item_name, $hinhanh, $introduce, $price, $item_id);

        // Thực thi câu lệnh SQL
        if (mysqli_stmt_execute($stmt)) {
            // Thành công
            echo "Sửa thông tin item thành công!";
        } else {
            // Lỗi khi thực thi câu lệnh SQL
            echo "Sửa thông tin item thất bại!";
        }

        // Đóng prepared statement
        mysqli_stmt_close($stmt);
    } else {
        // Lỗi khi chuẩn bị câu lệnh SQL
        echo "Lỗi sửa thông tin!";
    }
} else {
    echo "Có lỗi xảy ra!";
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>

