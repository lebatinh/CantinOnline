<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
$item_id = mysqli_real_escape_string($connect, $_POST['item_id']);
$quantity = mysqli_real_escape_string($connect, $_POST['quantity']);

// Kiểm tra xem item_id đã tồn tại trong giỏ hàng của acc_id chưa
$check_query = "SELECT quantity FROM cart WHERE acc_id = ? AND item_id = ?";
$check_stmt = mysqli_prepare($connect, $check_query);
mysqli_stmt_bind_param($check_stmt, "ss", $acc_id, $item_id);
mysqli_stmt_execute($check_stmt);
mysqli_stmt_store_result($check_stmt);

if (mysqli_stmt_num_rows($check_stmt) > 0) {
    // Nếu item_id đã tồn tại, cập nhật quantity
    mysqli_stmt_bind_result($check_stmt, $current_quantity);
    mysqli_stmt_fetch($check_stmt);
    $new_quantity = $current_quantity + $quantity;

    $update_query = "UPDATE cart SET quantity = ? WHERE acc_id = ? AND item_id = ?";
    $update_stmt = mysqli_prepare($connect, $update_query);
    mysqli_stmt_bind_param($update_stmt, "iss", $new_quantity, $acc_id, $item_id);

    if (mysqli_stmt_execute($update_stmt)) {
        echo "Cập nhật giỏ hàng thành công!";
    } else {
        echo "Cập nhật giỏ hàng thất bại!";
    }

    mysqli_stmt_close($update_stmt);
} else {
    // Nếu item_id chưa tồn tại, chèn vào bảng cart
    $insert_query = "INSERT INTO cart (acc_id, item_id, quantity) VALUES (?, ?, ?)";
    $insert_stmt = mysqli_prepare($connect, $insert_query);

    if ($insert_stmt) {
        mysqli_stmt_bind_param($insert_stmt, "ssi", $acc_id, $item_id, $quantity);

        if (mysqli_stmt_execute($insert_stmt)) {
            echo "Thêm vào giỏ hàng thành công!";
        } else {
            echo "Thêm vào giỏ hàng thất bại!";
        }

        mysqli_stmt_close($insert_stmt);
    } else {
        echo "Lỗi thêm vào giỏ hàng";
    }
}

mysqli_stmt_close($check_stmt);
mysqli_close($connect);
?>
