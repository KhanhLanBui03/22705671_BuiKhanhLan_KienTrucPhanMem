<?php
?>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Docker PHP</title>
  <style>
    body { font-family: Arial; text-align: center; padding: 50px; background: #eaf4fb; }
    h1 { color: #1a73e8; }
    .info { background: white; padding: 20px; border-radius: 8px; display: inline-block; margin-top: 20px; }
  </style>
</head>
<body>
  <h1>Hello, Docker PHP!</h1>
  <div class="info">
    <p><strong>PHP Version:</strong> <?php echo phpversion(); ?></p>
    <p><strong>Server:</strong> <?php echo $_SERVER['SERVER_SOFTWARE']; ?></p>
    <p><strong>Time:</strong> <?php echo date('Y-m-d H:i:s'); ?></p>
  </div>
</body>
</html>
