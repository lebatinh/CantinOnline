<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST tồn tại
if (isset($_POST['phonenumber']) && isset($_POST['password']) && isset($_POST['acc_id'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $phonenumber = mysqli_real_escape_string($connect, $_POST['phonenumber']);
    $password = mysqli_real_escape_string($connect, $_POST['password']);
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);

    // Sử dụng prepared statements để chèn dữ liệu vào bảng account
    $query = "SELECT * FROM account WHERE phonenumber = ? AND password = ? AND acc_id = ?";

    // Chuẩn bị câu lệnh SQL
    $stmt = mysqli_prepare($connect, $query);

    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
    if ($stmt) {
        // Gán các giá trị vào các tham số của câu lệnh SQL
        mysqli_stmt_bind_param($stmt, "sss", $phonenumber, $password, $acc_id);

        // Thực thi câu lệnh SQL
        if (mysqli_stmt_execute($stmt)) {
            // Bind kết quả trả về
            mysqli_stmt_store_result($stmt);

            // Kiểm tra số lượng dòng trả về
            if (mysqli_stmt_num_rows($stmt) > 0) {
                echo "Đăng nhập tài khoản thành công!";
                
                // Kiểm tra xem type và history có tồn tại và không rỗng
                if (isset($_POST['type']) && isset($_POST['history']) && !empty($_POST['type']) && !empty($_POST['history'])) {
                    $type = mysqli_real_escape_string($connect, $_POST['type']);
                    $history = mysqli_real_escape_string($connect, $_POST['history']);

                    // Thành công, thêm dữ liệu vào bảng account_history
                    $history_query = "INSERT INTO account_history (acc_id, type, history) VALUES (?, ?, ?)";
                    $history_stmt = mysqli_prepare($connect, $history_query);

                    if ($history_stmt) {
                        // Gán các giá trị vào các tham số của câu lệnh SQL
                        mysqli_stmt_bind_param($history_stmt, "sss", $acc_id, $type, $history);

                        // Thực thi câu lệnh SQL để thêm dữ liệu vào bảng account_history
                        mysqli_stmt_execute($history_stmt);

                        // Đóng prepared statement
                        mysqli_stmt_close($history_stmt);
                    }
                }
            } else {
                // Không tìm thấy dòng dữ liệu phù hợp
                echo "Đăng nhập thất bại!\nVui lòng kiểm tra lại dữ liệu nhập!";
            }
        } else {
            // Lỗi khi thực thi câu lệnh SQL
            echo "Đã có lỗi xảy ra!";
        }

        // Đóng prepared statement
        mysqli_stmt_close($stmt);
    } else {
        // Lỗi khi chuẩn bị câu lệnh SQL
        echo "Lỗi xác minh tài khoản!";
    }
} else {
    // Xử lý trường hợp khi tham số không tồn tại
    echo "Lỗi xác minh tài khoản!";
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
