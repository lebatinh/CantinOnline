<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
$product_type = mysqli_real_escape_string($connect, $_POST['product_type']);
$item_name = mysqli_real_escape_string($connect, $_POST['item_name']);
$hinhanh = mysqli_real_escape_string($connect, $_POST['hinhanh']);
$introduce = mysqli_real_escape_string($connect, $_POST['introduce']);
$price = mysqli_real_escape_string($connect, $_POST['price']);

// Kiểm tra xem acc_id có phải là "admin" hay không
if ($acc_id !== 'admin') {
    echo "Bạn không đủ thẩm quyền!";
    exit;
}

// Lấy ngày hiện tại
$date = new DateTime();
$current_date = $date->format('Ymd');

// Truy vấn để lấy hàng cuối cùng từ cột `stt` trong bảng
$last_stt_query = "SELECT stt FROM item ORDER BY stt DESC LIMIT 1";
$last_stt_result = mysqli_query($connect, $last_stt_query);

if ($last_stt_result && mysqli_num_rows($last_stt_result) > 0) {
    $last_stt_row = mysqli_fetch_assoc($last_stt_result);
    $last_stt = $last_stt_row['stt'];
    $new_stt = $last_stt + 1;
} else {
    $new_stt = 0;
}

// Tạo `item_id`
$item_id = $current_date . $new_stt;

// Sử dụng prepared statements để chèn dữ liệu vào bảng item
$query = "INSERT INTO item (product_type, item_id, item_name, hinhanh, introduce, price) VALUES (?, ?, ?, ?, ?, ?)";

// Chuẩn bị câu lệnh SQL
$stmt = mysqli_prepare($connect, $query);

// Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
if (!empty($product_type) && !empty($item_id) && !empty($item_name) && !empty($hinhanh) && !empty($introduce) && !empty($price)) {
    if ($stmt) {
        // Gán các giá trị vào các tham số của câu lệnh SQL
        mysqli_stmt_bind_param($stmt, "ssssss", $product_type, $item_id, $item_name, $hinhanh, $introduce, $price);

        // Thực thi câu lệnh SQL
        if (mysqli_stmt_execute($stmt)) {
            // Thành công
            echo "Đưa hàng lên sàn thành công!";
        } else {
            // Lỗi khi thực thi câu lệnh SQL
            echo "Đưa hàng lên sàn thất bại!";
        }

        // Đóng prepared statement
        mysqli_stmt_close($stmt);
    } else {
        // Lỗi khi chuẩn bị câu lệnh SQL
        echo "Lỗi đưa hàng lên sàn!";
    }
} else {
    // Xử lý trường hợp khi các tham số rỗng
    echo "null";
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
