<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST tồn tại
if (isset($_POST['acc_id']) && isset($_POST['phonenumber']) && isset($_POST['name']) && isset($_POST['password']) && isset($_POST['type']) && isset($_POST['history'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
    $phonenumber = mysqli_real_escape_string($connect, $_POST['phonenumber']);
    $name = mysqli_real_escape_string($connect, $_POST['name']);
    $password = mysqli_real_escape_string($connect, $_POST['password']);
    // Kiểm tra nếu cột isverify không có tham số được gửi, gán giá trị mặc định là false
    $isverify = isset($_POST['isverify']) ? mysqli_real_escape_string($connect, $_POST['isverify']) : "false";
    $type = mysqli_real_escape_string($connect, $_POST['type']);
    $history = mysqli_real_escape_string($connect, $_POST['history']);

    // Sử dụng prepared statements để chèn dữ liệu vào bảng account
    $query = "INSERT INTO account (acc_id, phonenumber, name, password, isverify) VALUES (?, ?, ?, ?, ?)";

    // Chuẩn bị câu lệnh SQL
    $stmt = mysqli_prepare($connect, $query);

    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
    if ($stmt) {
        // Gán các giá trị vào các tham số của câu lệnh SQL
        mysqli_stmt_bind_param($stmt, "sssss", $acc_id, $phonenumber, $name, $password, $isverify);

        // Thực thi câu lệnh SQL
        if (mysqli_stmt_execute($stmt)) {
            // Thành công, thêm dữ liệu vào bảng account_history
            $history_query = "INSERT INTO account_history (acc_id, type, history) VALUES (?, ?, ?)";
            $history_stmt = mysqli_prepare($connect, $history_query);

            if ($history_stmt) {
                // Gán các giá trị vào các tham số của câu lệnh SQL
                mysqli_stmt_bind_param($history_stmt, "sss", $acc_id, $type, $history);

                // Thực thi câu lệnh SQL
                if (mysqli_stmt_execute($history_stmt)) {
                    echo "Đăng ký tài khoản thành công!";
                }

                // Đóng prepared statement
                mysqli_stmt_close($history_stmt);
        } else {
            // Lỗi khi thực thi câu lệnh SQL
            echo "Đăng ký tài khoản thất bại!";
        }

        // Đóng prepared statement
        mysqli_stmt_close($stmt);
    } else {
        // Lỗi khi chuẩn bị câu lệnh SQL
        echo "Lỗi Đăng ký tài khoản!";
    }
} else {
    // Xử lý trường hợp khi tham số không tồn tại
    echo "Lỗi Đăng ký tài khoản!";
}
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
