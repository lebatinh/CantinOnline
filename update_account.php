<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST tồn tại
if (isset($_POST['acc_id']) && isset($_POST['phonenumber']) && isset($_POST['newpassword']) && isset($_POST['history'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
    $phonenumber = mysqli_real_escape_string($connect, $_POST['phonenumber']);
    $password = mysqli_real_escape_string($connect, $_POST['newpassword']);
    $type = "Đổi mật khẩu"; 
    $history = mysqli_real_escape_string($connect, $_POST['history']);

    // Sử dụng prepared statements để cập nhật dữ liệu trong bảng account
    $query = "UPDATE account SET password = ? WHERE acc_id = ? AND phonenumber = ?";

    // Chuẩn bị câu lệnh SQL
    $stmt = mysqli_prepare($connect, $query);

    if (!empty($acc_id) && !empty($phonenumber)) {
        // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
        if ($stmt) {
            // Gán các giá trị vào các tham số của câu lệnh SQL
            mysqli_stmt_bind_param($stmt, "sss", $password, $acc_id, $phonenumber);

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
                        echo "Mật khẩu đã được thay đổi thành công. Vui lòng sử dụng để đăng nhập!";
                    }

                    // Đóng prepared statement
                    mysqli_stmt_close($history_stmt);
                }
            } else {
                // Lỗi khi thực thi câu lệnh SQL
                echo "Thay đổi mật khẩu thất bại. Vui lòng thử lại sau!";
            }

            // Đóng prepared statement
            mysqli_stmt_close($stmt);
        } else {
            // Lỗi khi chuẩn bị câu lệnh SQL
            echo "Thay đổi mật khẩu thất bại. Vui lòng thử lại sau!";
        }
    } else {
        echo "Lỗi thay đổi mật khẩu. Vui lòng kiểm tra lại thông tin!";
    }
} else {
    echo "Vui lòng điền đầy đủ thông tin!";
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
