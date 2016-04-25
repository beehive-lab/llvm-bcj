define i32 @main() {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4 
  store i32 0, i32* %1, align 4
  store i32 0, i32* %2, align 4
  %3 = load i32, i32* %1, align 4
  %4 = load i32, i32* %2, align 4
  %5 = add i32 %3, %4
  %6 = add nsw i32 %3, %4
  %7 = add nuw i32 %3, %4
  %8 = add nuw nsw i32 %3, %4
  %9 = sub i32 %3, %4
  %10 = sub nsw i32 %3, %4
  %11 = mul i32 %3, %4
  %12 = mul nsw i32 %3, %4
  %13 = udiv i32 %3, %4
  %14 = udiv exact i32 %3, %4
  %15 = sdiv i32 %3, %4
  %16 = sdiv exact i32 %3, %4
  %17 = urem i32 %3, %4
  %18 = srem i32 %3, %4
  %19 = shl i32 %3, %4
  %20 = shl nsw i32 %3, %4
  %21 = lshr i32 %3, %4
  %22 = lshr exact i32 %3, %4
  %23 = ashr i32 %3, %4
  %24 = ashr exact i32 %3, %4
  %25 = and i32 %3, %4
  %26 = or i32 %3, %4
  %27 = xor i32 %3, %4
  %28 = icmp eq i32 %3, %4
  %29 = icmp ne i32 %3, %4
  %30 = icmp ugt i32 %3, %4
  %31 = icmp uge i32 %3, %4
  %32 = icmp ult i32 %3, %4
  %33 = icmp ule i32 %3, %4
  %34 = icmp sgt i32 %3, %4
  %35 = icmp sge i32 %3, %4
  %36 = icmp slt i32 %3, %4
  %37 = icmp sle i32 %3, %4
  ret i32 0
}
