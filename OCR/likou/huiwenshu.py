def isPalindrome(x):
    """
    :type x: int
    :rtype: bool
    """
    if x < 0:
        return False
    else:
        each = []
        while x > 0:
            each.append(x % 10)
            x = (int)(x / 10)

        length = len(each)

        flag = True

        for i in range(0, length):
            if each[i] == each[length-1-i]:
                continue
            else:
                flag = False
                break
        return flag

if __name__ == "__main__":
    print(isPalindrome(121))