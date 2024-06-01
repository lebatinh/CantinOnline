<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$id = mysqli_real_escape_string($connect, $_POST['id']);
$item_id = mysqli_real_escape_string($connect, $_POST['item_id']);
$item_name = mysqli_real_escape_string($connect, $_POST['item_name']);
$acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
$name_user = mysqli_real_escape_string($connect, $_POST['name_user']);
$review = mysqli_real_escape_string($connect, $_POST['review']);
$image = mysqli_real_escape_string($connect, $_POST['image']);
$reviewscore = mysqli_real_escape_string($connect, $_POST['reviewscore']);
$timereview = mysqli_real_escape_string($connect, $_POST['timereview']);

// Sử dụng prepared statements để chèn dữ liệu vào bảng account
$query = "INSERT INTO review (id, item_id, item_name, acc_id, name_user, review, image, reviewscore, timereview) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

// Chuẩn bị câu lệnh SQL
$stmt = mysqli_prepare($connect, $query);

// Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
if (!empty($id) && !empty($id) && !empty($item_id) && !empty($item_name) && !empty($acc_id) && !empty($name_user) && !empty($timereview)){
    if ($stmt) {
    // Gán các giá trị vào các tham số của câu lệnh SQL
    mysqli_stmt_bind_param($stmt, "sssssssss", $id, $item_id, $item_name, $acc_id, $name_user, $review, $image, $reviewscore, $timereview);

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

} else {
    // Xử lý trường hợp khi các tham số rỗng
    echo "null";
}
// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
