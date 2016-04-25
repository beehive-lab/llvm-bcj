define i32 @main() {
  %1 = alloca i8
  %2 = alloca i8, align 1
  %3 = alloca i8, align 2
  %4 = alloca i8, align 4
  %5 = alloca i8, align 8
  %6 = alloca i8, align 16
  %7 = alloca i8, i32 4
  %8 = alloca i8, i32 4, align 4
  %9 = alloca i32
  store i32 0, i32* %9
  store volatile i32 0, i32* %9
  %10 = load i32, i32* %9
  %11 = load volatile i32, i32* %9
  %12 = alloca i32, align 4
  store i32 0, i32* %12, align 4
  store volatile i32 0, i32* %12, align 4
  %13 = load i32, i32* %12, align 4
  %14 = load volatile i32, i32* %12, align 4
  ret i32 0
}
