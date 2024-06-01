<?php
require "DBConnect.php";

// Kiểm tra xem các giá trị POST tồn tại
if (isset($_POST['type']) && isset($_POST['title']) && isset($_POST['notification']) && isset($_POST['timepost']) && isset($_POST['receiver'])) {
    // Lấy dữ liệu từ yêu cầu POST và xử lý trước khi sử dụng trong câu lệnh SQL
    $type = mysqli_real_escape_string($connect, $_POST['type']);
    $title = mysqli_real_escape_string($connect, $_POST['title']);
    $notification = mysqli_real_escape_string($connect, $_POST['notification']);
    $timepost = mysqli_real_escape_string($connect, $_POST['timepost']);
    $receiver = mysqli_real_escape_string($connect, $_POST['receiver']);

    // Kiểm tra nếu receiver là "all" thì gán giá trị là "all", ngược lại thì xử lý danh sách JSON
    if ($receiver === "all") {
        $receiverValue = "all";
    } else {
        // Xử lý danh sách mã người nhận
        $receiverListJson = isset($_POST['receiver']) ? $_POST['receiver'] : null;
        $receiverList = json_decode($receiverListJson, true);

        if ($receiverList !== null) {
            // Kiểm tra xem $receiverList có giá trị hợp lệ không
            if (!empty($receiverList)) {
                // Chuyển danh sách thành một chuỗi phân tách bằng dấu phẩy
                $receiverValue = implode(",", $receiverList);
            } else {
                // Trường hợp $receiverList rỗng
                echo "Danh sách người nhận rỗng.";
                exit();
            }
        } else {
            // Trường hợp $receiverList không hợp lệ
            echo "Dữ liệu người nhận không hợp lệ.";
            exit();
        }
    }

    // Sử dụng prepared statements để chèn dữ liệu vào bảng account
    $query = "INSERT INTO notification (type, title, notification, timepost, receiver) VALUES (?, ?, ?, ?, ?)";

    // Chuẩn bị câu lệnh SQL
    $stmt = mysqli_prepare($connect, $query);

    // Kiểm tra xem câu lệnh SQL đã được chuẩn bị thành công hay không
    if ($stmt) {
        // Gán các giá trị vào các tham số của câu lệnh SQL
        mysqli_stmt_bind_param($stmt, "sssss", $type, $title, $notification, $timepost, $receiverValue);

        // Thực thi câu lệnh SQL
        if (mysqli_stmt_execute($stmt)) {
            // Thành công
            echo "Gửi thông báo thành công!";
        } else {
            // Lỗi khi thực thi câu lệnh SQL
            echo "Gửi thông báo thất bại!";
        }

        // Đóng prepared statement
        mysqli_stmt_close($stmt);
    } else {
        // Lỗi khi chuẩn bị câu lệnh SQL
        echo "Lỗi Gửi thông báo!";
    }
} else {
    // Xử lý trường hợp khi tham số không tồn tại
    echo "Lỗi Gửi thông báo!";
}

// Đóng kết nối với cơ sở dữ liệu
mysqli_close($connect);
?>
