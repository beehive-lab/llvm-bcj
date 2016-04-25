define i32 @testSelect(i1 %cond) {
  %1 = alloca i1
  store i1 %cond, i1* %1
  %2 = load i1, i1* %1
  %3 = select i1 %2, i32 0, i32 1
  ret i32 %3
}

define <4 x i32> @testVectorSelect(<4 x i1> %cond) {
  %1 = alloca <4 x i1>
  store <4 x i1> %cond, <4 x i1>* %1
  %2 = load <4 x i1>, <4 x i1>* %1
  %3 = select <4 x i1> %2, <4 x i32> <i32 0, i32 0, i32 0, i32 0>, <4 x i32> <i32 1, i32 1, i32 1, i32 1>
  ret <4 x i32> %3
}

define i32 @testSwitch(i32 %cond) {
  switch i32 %cond, label %label_d [ i32 0, label %label_a i32 1, label %label_b i32 2, label %label_c ]

label_a:
  %1 = alloca i32
  store i32 9, i32* %1
  %2 = load i32, i32* %1
  br label %label_e

label_b:
  %3 = alloca i32
  store i32 99, i32* %3
  %4 = load i32, i32* %3
  br label %label_e

label_c:
  %5 = alloca i32
  store i32 999, i32* %5
  %6 = load i32, i32* %5
  br label %label_e

label_d:
  %7 = alloca i32
  store i32 9999, i32* %7
  %8 = load i32, i32* %7
  br label %label_e

label_e:
  %9 = phi i32 [ %2, %label_a ], [ %4, %label_b ], [ %6, %label_c ], [ %8, %label_d ]
  ret i32 %9
}
