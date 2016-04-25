define i32 @test() {
  %1 = alloca i32, align 4
  %2 = icmp eq i32 0, 0 
  br i1 %2, label %label_a, label %label_b

label_a:
  store i32 0, i32* %1, align 4
  br label %label_c
  
label_b:
  store i32 1, i32* %1, align 4
  br label %label_c

label_c:
  %3 = load i32, i32* %1, align 4
  ret i32 %3
}

define i32 @testPhi() {
  %1 = icmp eq i32 0, 0 
  br i1 %1, label %label_a, label %label_b

label_a:
  %2 = alloca i32, align 4
  store i32 0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  br label %label_c
  
label_b:
  %4 = alloca i32, align 4
  store i32 0, i32* %4, align 4
  %5 = load i32, i32* %4, align 4
  br label %label_c

label_c:
  %6 = phi i32 [ %3, %label_a ], [ %5, %label_b ]
  ret i32 %6
}

define i32 @testComplexPhi(i1 %cond1, i1 %cond2) {
  br i1 %cond1, label %label_a, label %label_d

label_a:
  br i1 %cond2, label %label_b, label %label_c

label_b:
  %1 = alloca i32, align 4
  store i32 0, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  br label %label_g
  
label_c:
  %3 = alloca i32, align 4
  store i32 0, i32* %3, align 4
  %4 = load i32, i32* %3, align 4
  br label %label_g

label_d:
  br i1 %cond2, label %label_e, label %label_f

label_e:
  %5 = alloca i32, align 4
  store i32 0, i32* %5, align 4
  %6 = load i32, i32* %5, align 4
  br label %label_g
  
label_f:
  %7 = alloca i32, align 4
  store i32 0, i32* %7, align 4
  %8 = load i32, i32* %7, align 4
  br label %label_g

label_g:
  %9 = phi i32 [ %2, %label_b ], [ %4, %label_c ], [ %6, %label_e ], [ %8, %label_f ]
  ret i32 %9
}

define i32 @testIndirectBranch(i8* %addr) {
  %1 = alloca i32
  indirectbr i8* %addr, [ label %label_a, label %label_b ]

label_a:
  store i32 0, i32* %1
  br label %label_c
  
label_b:
  store i32 0, i32* %1
  br label %label_c

label_c:
  %2 = load i32, i32* %1
  ret i32 %2
}
