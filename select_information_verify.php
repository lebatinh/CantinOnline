<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST tồn tại
if (isset($_POST['acc_id'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $acc_id = mysqli_real_escape_string($connect, $_POST['acc_id']);

    // Sử dụng prepared statements để lấy dữ liệu từ bảng account
    $query = "SELECT phonenumber, name FROM account WHERE acc_id = ?";

    // Chuẩn bị câu lệnh SQL
    if ($stmt = mysqli_prepare($connect, $query)) {
        // Gán các giá trị vào các tham số của câu lệnh SQL
        mysqli_stmt_bind_param($stmt, "s", $acc_id);

        // Thực thi câu lệnh SQL
        if (mysqli_stmt_execute($stmt)) {
            // Lấy kết quả trả về
            $result = mysqli_stmt_get_result($stmt);

            // Kiểm tra số lượng dòng trả về
            if (mysqli_num_rows($result) > 0) {
                // Lấy tất cả dữ liệu trả về
                $data = mysqli_fetch_all($result, MYSQLI_ASSOC);
                // Trả về dữ liệu dưới dạng JSON
                echo json_encode(["data" => $data]);
            } else {
                // Không tìm thấy dòng dữ liệu phù hợp
                echo json_encode(["message" => "Tài khoản chưa xác thực!"]);
            }

            // Giải phóng bộ nhớ kết quả
            mysqli_free_result($result);
        } else {
            // Lỗi khi thực thi câu lệnh SQL
            echo json_encode(["message" => "Đã có lỗi xảy ra!"]);
        }

        // Đóng prepared statement
        mysqli_stmt_close($stmt);
    } else {
        // Lỗi khi chuẩn bị câu lệnh SQL
        echo json_encode(["message" => "Lỗi khi chuẩn bị!"]);
    }
} else {
    // Xử lý trường hợp khi tham số không tồn tại
    echo json_encode(["message" => "Lỗi server không nhận được dữ liệu!"]);
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
