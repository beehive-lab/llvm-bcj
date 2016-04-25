%test.struct = type { i32, { i32 } }

@test = global %test.struct zeroinitializer

define i32 @main() {
  %1 = extractvalue [4 x i32] [i32 0, i32 1, i32 2, i32 3], 0
  %2 = insertvalue [1 x i32] undef, i32 0, 0
  %3 = load %test.struct, %test.struct* @test
  %4 = extractvalue %test.struct %3, 0
  %5 = insertvalue %test.struct %3, i32 0, 0
  ret i32 0
}
