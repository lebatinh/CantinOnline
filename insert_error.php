<?php
require "DBConnect.php";

// Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
$acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
$error = mysqli_real_escape_string($connect, $_POST['error']);
$describeError = mysqli_real_escape_string($connect, $_POST['describeError']);
$timeError = mysqli_real_escape_string($connect, $_POST['timeError']);
$image1 = mysqli_real_escape_string($connect, $_POST['image1']);
$image2 = mysqli_real_escape_string($connect, $_POST['image2']);

// Sử dụng prepared statements để chèn dữ liệu vào bảng account
$query = "INSERT INTO error (acc_id, error, describeError, timeError, image1, image2) VALUES (?, ?, ?, ?, ?, ?)";

// Chuẩn bị câu lệnh SQL
$stmt = mysqli_prepare($connect, $query);

// Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
if (!empty($acc_id) && !empty($error) && !empty($describeError) && !empty($timeError) && !empty($image1) && !empty($image2)){
    if ($stmt) {
    // Gán các giá trị vào các tham số của câu lệnh SQL
    mysqli_stmt_bind_param($stmt, "ssssss", $acc_id, $error, $describeError, $timeError, $image1, $image2);

    // Thực thi câu lệnh SQL
    if (mysqli_stmt_execute($stmt)) {
        // Thành công
        echo "Báo lỗi thành công. Chúng tôi sẽ khắc phục lỗi sớm nhất có thể!";
    } else {
        // Lỗi khi thực thi câu lệnh SQL
        echo "Báo lỗi thất bại!";
    }

    // Đóng prepared statement
    mysqli_stmt_close($stmt);
} else {
    // Lỗi khi chuẩn bị câu lệnh SQL
    echo "Lỗi báo lỗi!";
}

} else {
    // Xử lý trường hợp khi các tham số rỗng
    echo "null";
}
// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
