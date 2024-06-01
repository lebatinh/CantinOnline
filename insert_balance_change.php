<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST tồn tại
if (isset($_POST['acc_id']) && isset($_POST['receiver']) && isset($_POST['money_transaction']) && isset($_POST['time_transaction']) && isset($_POST['type'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);
    $money_transaction = mysqli_real_escape_string($connect, $_POST['money_transaction']);
    $receiver =  mysqli_real_escape_string($connect, $_POST['receiver']);
    $time_transaction = mysqli_real_escape_string($connect, $_POST['time_transaction']);
    $type = mysqli_real_escape_string($connect, $_POST['type']);
    $descriptions = mysqli_real_escape_string($connect, $_POST['descriptions']);

    // Kiểm tra nếu người gửi và người nhận là cùng một người
    if ($acc_id === $receiver) {
        echo "Người gửi và người nhận không thể là cùng một người";
        exit; // Dừng thực thi mã tiếp theo
    }

    // Truy vấn để lấy số dư hiện tại của người gửi
    $query = "SELECT sodu FROM finance WHERE acc_id = ?";
    $stmt = mysqli_prepare($connect, $query);
    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "s", $acc_id);
        mysqli_stmt_execute($stmt);
        mysqli_stmt_bind_result($stmt, $sodu);
        mysqli_stmt_fetch($stmt);
        mysqli_stmt_close($stmt);

        // Kiểm tra nếu số dư nhỏ hơn số tiền giao dịch
        if ($sodu < $money_transaction) {
            echo "Số tiền giao dịch không đủ";
            exit; // Dừng thực thi mã tiếp theo
        }
    } else {
        echo "Lỗi khi truy vấn số dư!";
        exit;
    }

    // Sử dụng prepared statements để chèn dữ liệu vào bảng balance_change
    $query = "INSERT INTO balance_change (acc_id, money_transaction, receiver, time_transaction, type, descriptions, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

    // Chuẩn bị câu lệnh SQL
    $stmt = mysqli_prepare($connect, $query);

    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
    if ($stmt) {
        // Ban đầu, đặt trạng thái giao dịch là "Đang xử lý"
        $status = "Đang xử lý";
        // Gán các giá trị vào các tham số của câu lệnh SQL
        mysqli_stmt_bind_param($stmt, "sssssss", $acc_id, $money_transaction, $receiver, $time_transaction, $type, $descriptions, $status);

        // Thực thi câu lệnh SQL
        if (mysqli_stmt_execute($stmt)) {
            // Cập nhật số dư của người gửi và người nhận
            $update_sender_query = "UPDATE finance SET sodu = sodu - ? WHERE acc_id = ?";
            $update_receiver_query = "UPDATE finance SET sodu = sodu + ? WHERE acc_id = ?";

            // Chuẩn bị câu lệnh SQL cho người gửi
            $sender_stmt = mysqli_prepare($connect, $update_sender_query);
            if ($sender_stmt) {
                mysqli_stmt_bind_param($sender_stmt, "ds", $money_transaction, $acc_id);
                // Chuẩn bị câu lệnh SQL cho người nhận
                $receiver_stmt = mysqli_prepare($connect, $update_receiver_query);
                if ($receiver_stmt) {
                    mysqli_stmt_bind_param($receiver_stmt, "ds", $money_transaction, $receiver);

                    // Bắt đầu transaction
                    mysqli_begin_transaction($connect);
                    
                    // Thực thi câu lệnh SQL cho người gửi và người nhận
                    if (mysqli_stmt_execute($sender_stmt) && mysqli_stmt_execute($receiver_stmt)) {
                        // Nếu cả hai câu lệnh đều thành công, commit transaction
                        mysqli_commit($connect);
                        // Cập nhật trạng thái giao dịch thành công
                        $status = "Giao dịch thành công";
                        echo "Giao dịch thành công!";
                    } else {
                        // Nếu có lỗi, rollback transaction
                        mysqli_rollback($connect);
                        // Cập nhật trạng thái giao dịch thất bại
                        $status = "Giao dịch thất bại";
                        echo "Giao dịch thất bại!";
                    }

                    // Đóng prepared statements
                    mysqli_stmt_close($receiver_stmt);
                } else {
                    echo "Lỗi khi chuẩn bị câu lệnh SQL cho người nhận!";
                }
                mysqli_stmt_close($sender_stmt);
            } else {
                echo "Lỗi khi chuẩn bị câu lệnh SQL cho người gửi!";
            }

            // Cập nhật trạng thái giao dịch trong bảng balance_change
            $update_status_query = "UPDATE balance_change SET status = ? WHERE acc_id = ? AND time_transaction = ?";
            $status_stmt = mysqli_prepare($connect, $update_status_query);
            if ($status_stmt) {
                mysqli_stmt_bind_param($status_stmt, "sss", $status, $acc_id, $time_transaction);
                mysqli_stmt_execute($status_stmt);
                mysqli_stmt_close($status_stmt);
            }
        } else {
            // Lỗi khi thực thi câu lệnh SQL
            echo "Giao dịch thất bại!";
        }

        // Đóng prepared statement
        mysqli_stmt_close($stmt);
    } else {
        // Lỗi khi chuẩn bị câu lệnh SQL
        echo "Lỗi giao dịch!";
    }
} else {
    // Xử lý trường hợp khi tham số không tồn tại
    echo "Giao dịch không thể thực hiện!";
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
