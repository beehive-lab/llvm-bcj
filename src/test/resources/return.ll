define i32 @main() { 
  ret i32 0
}

define i32 @testVariable() {
  %1 = alloca i32, align 4
  store i32 0, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  ret i32 %2
}

define void @testVoid() {
  ret void
}

