# Experiment 2 - Install a C compiler in the VM and run a program

## Aim
To install a C compiler (GCC) in the virtual machine created using VirtualBox and execute a simple C program.

## Procedure
1. Start the Ubuntu VM in VirtualBox (created in Experiment 1).
2. Open the Terminal (Ctrl+Alt+T).
3. Check the C compiler: `gcc --version` (GCC 11.4 is installed; if missing, install it with `sudo apt install -y gcc`).
4. Create a folder and the program: `mkdir cprog && cd cprog`, then `gedit hello.c`.
5. Type the C program in gedit and save it (Ctrl+S).
6. Compile it: `gcc hello.c` - this produces `a.out`.
7. Run it: `./a.out` and observe the output.

## Source code

**`hello.c`**

```
#include <stdio.h>

int main()
{
	int a = 10, b = 20;
	printf("Hello, World! C program running inside the Ubuntu VM\n");
	printf("Sum of %d and %d = %d\n", a, b, a + b);
	return 0;
}
```
## Result
GCC was installed in the Ubuntu VM and the C program compiled and executed successfully.
