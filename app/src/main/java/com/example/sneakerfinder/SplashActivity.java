package com.example.sneakerfinder;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.provider.SyncStateContract;

import com.daimajia.androidanimations.library.Techniques;

import com.example.sneakerfinder.ui.main_activity.MainActivity;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.lightgrey); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(1500); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP


        //Customize Path
        configSplash.setPathSplash("M 114.00,296.00 C 123.47,302.84 128.59,315.39 143.00,316.10 147.22,316.30 150.66,315.27 153.98,312.61 155.83,311.13 157.65,309.16 158.68,307.00 161.42,301.26 157.67,296.11 153.91,292.00 146.19,283.56 136.56,276.85 130.47,267.00 116.94,245.14 122.01,222.44 133.26,201.00 144.32,179.92 165.95,165.04 190.00,165.00 201.96,164.98 207.36,164.82 219.00,169.05 222.41,170.29 226.99,171.76 228.82,175.11 230.81,178.74 228.45,187.79 227.75,192.00 225.37,206.30 224.52,221.18 220.00,235.00 212.85,229.04 206.84,216.87 195.00,215.37 187.02,214.35 174.53,222.60 181.65,233.00 185.61,238.77 194.81,245.90 200.00,251.01 210.40,261.29 216.97,273.09 216.97,288.00 216.97,288.00 216.16,296.00 216.16,296.00 215.58,301.43 215.06,306.72 213.57,312.00 207.51,333.47 191.76,352.14 171.00,360.55 159.46,365.22 150.25,366.14 138.00,366.00 132.24,365.93 119.63,363.67 114.00,362.08 110.94,361.21 106.94,359.74 105.33,356.79 103.38,353.22 105.68,343.15 106.42,339.00 106.42,339.00 114.00,296.00 114.00,296.00 Z M 1038.00,296.00 C 1045.75,299.99 1051.80,314.81 1066.00,316.44 1075.06,317.49 1088.17,307.79 1081.66,297.00 1077.92,290.81 1071.15,286.11 1066.00,281.00 1055.43,270.50 1047.18,258.39 1047.00,243.00 1046.91,234.77 1046.83,230.18 1048.66,222.00 1053.51,200.38 1070.24,177.93 1091.00,169.45 1102.53,164.74 1109.92,164.86 1122.00,165.00 1129.48,165.09 1138.07,167.01 1145.00,169.82 1147.85,170.98 1151.23,172.39 1152.82,175.19 1154.94,178.89 1152.63,187.72 1151.92,192.00 1151.92,192.00 1145.00,235.00 1145.00,235.00 1137.30,231.04 1125.46,208.05 1110.02,217.09 1108.25,218.12 1106.85,219.39 1105.65,221.04 1099.48,229.50 1108.24,236.45 1114.00,241.96 1130.31,257.57 1141.28,265.37 1141.00,290.00 1140.91,297.39 1138.75,309.00 1136.31,316.00 1120.71,360.71 1071.87,377.36 1030.00,359.00 1028.95,360.54 1028.63,361.42 1026.78,362.26 1024.07,363.49 1008.06,363.00 1004.00,363.00 1004.00,363.00 982.00,363.00 982.00,363.00 979.41,363.00 975.51,363.22 973.27,361.83 969.31,359.35 967.86,347.58 967.08,343.00 967.08,343.00 961.00,312.00 961.00,312.00 959.74,305.71 959.10,298.64 956.00,293.00 956.00,293.00 949.13,338.00 949.13,338.00 948.50,342.51 946.97,356.60 944.86,359.77 942.39,363.50 937.92,362.99 934.00,363.00 934.00,363.00 901.00,363.00 901.00,363.00 898.50,363.00 893.70,363.38 891.99,361.26 889.78,358.53 891.95,351.18 892.54,348.00 892.54,348.00 897.13,317.00 897.13,317.00 897.13,317.00 907.58,251.00 907.58,251.00 907.58,251.00 921.00,168.00 921.00,168.00 921.00,168.00 985.00,168.00 985.00,168.00 1001.01,168.19 1021.93,173.72 1032.82,186.00 1047.74,202.84 1044.62,237.32 1034.68,256.00 1031.03,262.85 1025.12,269.74 1019.00,274.47 1015.45,277.22 1007.87,280.51 1006.92,285.01 1006.28,288.07 1010.81,300.43 1012.00,304.00 1012.00,304.00 1028.00,351.00 1028.00,351.00 1028.00,351.00 1032.25,328.00 1032.25,328.00 1032.25,328.00 1038.00,296.00 1038.00,296.00 Z M 247.00,169.00 C 263.32,167.68 281.76,166.86 298.00,169.00 298.00,169.00 300.49,177.00 300.49,177.00 300.49,177.00 304.00,195.00 304.00,195.00 304.00,195.00 313.20,240.00 313.20,240.00 313.20,240.00 320.00,272.00 320.00,272.00 320.00,272.00 321.00,254.00 321.00,254.00 321.19,238.09 328.88,185.34 332.00,168.00 332.00,168.00 371.00,168.00 371.00,168.00 374.07,168.00 381.03,167.74 382.95,170.56 384.04,172.19 383.09,178.69 382.95,181.00 382.95,181.00 378.08,214.00 378.08,214.00 378.08,214.00 359.58,331.00 359.58,331.00 359.58,331.00 354.00,362.00 354.00,362.00 341.13,363.70 327.98,363.02 315.00,363.00 311.76,362.99 306.12,363.17 303.51,361.07 301.54,359.48 300.96,356.35 300.42,354.00 300.42,354.00 297.00,337.00 297.00,337.00 297.00,337.00 288.25,291.00 288.25,291.00 287.00,283.54 284.93,268.20 282.00,262.00 282.00,262.00 279.94,278.00 279.94,278.00 279.94,278.00 279.94,289.00 279.94,289.00 279.94,289.00 272.27,339.00 272.27,339.00 271.55,343.90 270.49,359.22 266.58,361.83 264.46,363.24 260.51,363.00 258.00,363.00 248.59,363.02 223.67,363.87 216.00,362.00 216.00,362.00 230.58,267.00 230.58,267.00 230.58,267.00 247.00,169.00 247.00,169.00 Z M 370.00,363.00 C 370.00,363.00 379.73,298.00 379.73,298.00 379.73,298.00 387.08,249.00 387.08,249.00 387.08,249.00 389.72,236.00 389.72,236.00 389.72,236.00 392.92,213.00 392.92,213.00 392.92,213.00 395.73,197.00 395.73,197.00 395.73,197.00 398.00,181.00 398.00,181.00 398.55,178.30 399.68,171.68 401.72,169.95 404.11,167.94 410.93,168.00 414.00,168.00 414.00,168.00 499.00,168.00 499.00,168.00 499.00,168.00 494.87,200.00 494.87,200.00 494.42,203.15 493.16,211.20 491.15,213.44 488.90,215.94 484.12,215.96 481.00,216.00 481.00,216.00 462.00,216.00 462.00,216.00 458.62,216.02 452.09,216.08 449.58,218.58 447.25,220.92 445.11,235.80 444.00,240.00 444.00,240.00 474.00,240.00 474.00,240.00 476.66,240.01 481.01,239.61 482.34,242.43 483.59,244.75 482.66,250.44 482.34,253.00 482.34,253.00 478.42,276.00 478.42,276.00 477.94,279.13 477.73,283.99 475.30,286.26 473.06,288.35 468.88,288.00 466.00,288.00 466.00,288.00 436.00,288.00 436.00,288.00 436.00,288.00 432.00,315.00 432.00,315.00 432.00,315.00 465.00,315.00 465.00,315.00 468.10,315.01 472.85,314.53 474.87,317.43 476.65,320.11 475.42,324.98 474.87,328.00 474.87,328.00 471.13,351.00 471.13,351.00 470.74,353.80 470.31,359.25 468.27,361.26 466.14,363.35 461.79,363.00 459.00,363.00 459.00,363.00 370.00,363.00 370.00,363.00 Z M 473.00,362.00 C 473.09,352.72 475.83,348.43 479.20,340.00 479.20,340.00 490.40,312.00 490.40,312.00 490.40,312.00 530.99,210.00 530.99,210.00 530.99,210.00 542.20,182.00 542.20,182.00 543.40,179.02 546.21,171.79 548.63,169.95 551.29,167.94 557.72,168.01 561.00,168.00 561.00,168.00 614.00,168.00 614.00,168.00 614.00,168.00 615.00,183.00 615.00,183.00 615.00,183.00 617.00,213.00 617.00,213.00 617.00,213.00 617.00,222.00 617.00,222.00 617.00,222.00 617.91,232.00 617.91,232.00 617.91,232.00 619.00,257.00 619.00,257.00 619.00,257.00 620.00,272.00 620.00,272.00 620.00,272.00 621.00,291.00 621.00,291.00 621.00,291.00 622.00,307.00 622.00,307.00 622.00,307.00 623.00,333.00 623.00,333.00 623.00,333.00 623.95,343.00 623.95,343.00 623.95,343.00 623.95,356.00 623.95,356.00 623.96,357.96 624.15,360.47 622.43,361.83 620.62,363.25 616.26,363.00 614.00,363.00 614.00,363.00 578.00,363.00 578.00,363.00 575.07,362.98 571.05,363.30 569.17,360.57 567.26,357.78 568.00,342.22 568.00,338.00 568.00,338.00 542.00,338.00 542.00,338.00 540.19,338.04 537.98,337.98 536.42,339.02 532.45,341.66 531.44,355.82 527.03,360.47 524.47,363.17 519.46,362.98 516.00,363.00 504.80,363.05 483.04,363.80 473.00,362.00 Z M 657.00,169.00 C 657.00,169.00 696.00,168.00 696.00,168.00 696.00,168.00 712.00,169.00 712.00,169.00 712.00,169.00 705.08,214.00 705.08,214.00 704.04,220.31 701.46,231.11 703.00,237.00 704.56,231.64 708.18,226.13 710.75,221.00 710.75,221.00 724.75,193.00 724.75,193.00 726.61,189.30 735.54,172.62 737.79,170.56 740.58,167.99 746.39,168.02 750.00,168.00 750.00,168.00 795.00,168.00 795.00,168.00 792.93,176.81 786.48,185.20 781.80,193.00 781.80,193.00 754.80,238.00 754.80,238.00 751.31,243.74 740.23,258.96 741.06,265.16 741.06,265.16 749.98,295.00 749.98,295.00 749.98,295.00 763.42,340.00 763.42,340.00 763.42,340.00 770.00,362.00 770.00,362.00 762.21,363.89 732.02,363.02 722.00,363.00 719.41,363.00 715.51,363.22 713.27,361.83 709.23,359.29 707.75,346.74 706.80,342.00 706.80,342.00 699.21,306.00 699.21,306.00 697.75,299.12 697.00,291.34 694.00,285.00 694.00,285.00 689.25,313.00 689.25,313.00 689.25,313.00 681.00,363.00 681.00,363.00 681.00,363.00 650.00,363.00 650.00,363.00 641.53,363.00 635.21,363.71 627.00,361.00 627.00,361.00 630.28,334.00 630.28,334.00 630.28,334.00 638.27,285.00 638.27,285.00 638.27,285.00 654.27,183.00 654.27,183.00 654.27,183.00 657.00,169.00 657.00,169.00 Z M 776.00,363.00 C 776.00,363.00 789.58,273.00 789.58,273.00 789.58,273.00 798.75,213.00 798.75,213.00 798.75,213.00 801.58,197.00 801.58,197.00 801.58,197.00 803.89,181.00 803.89,181.00 804.49,178.37 805.77,171.61 807.72,169.95 810.11,167.94 816.93,168.00 820.00,168.00 820.00,168.00 905.00,168.00 905.00,168.00 905.00,168.00 900.87,200.00 900.87,200.00 900.42,203.15 899.16,211.20 897.15,213.44 894.90,215.94 890.12,215.96 887.00,216.00 887.00,216.00 868.00,216.00 868.00,216.00 864.47,216.02 858.22,216.09 855.51,218.58 851.97,221.84 850.67,235.90 850.00,241.00 850.00,241.00 873.00,240.00 873.00,240.00 873.00,240.00 890.00,241.00 890.00,241.00 890.00,241.00 885.00,273.00 885.00,273.00 884.45,277.00 884.06,283.66 880.72,286.26 878.50,287.99 875.67,287.96 873.00,288.00 873.00,288.00 842.00,288.00 842.00,288.00 842.00,288.00 838.00,315.00 838.00,315.00 838.00,315.00 871.00,315.00 871.00,315.00 874.07,315.00 878.88,314.53 880.82,317.43 882.73,320.27 880.66,329.49 880.08,333.00 880.08,333.00 877.00,352.00 877.00,352.00 876.56,355.05 876.09,359.16 873.57,361.26 871.12,363.31 867.03,363.00 864.00,363.00 864.00,363.00 776.00,363.00 776.00,363.00 Z M 961.00,257.00 C 965.12,256.99 968.06,257.13 972.00,255.52 984.04,250.60 991.85,232.05 986.35,220.00 982.72,212.04 971.87,210.38 969.14,213.57 967.60,215.37 967.21,219.68 966.87,222.00 966.87,222.00 964.25,237.00 964.25,237.00 964.25,237.00 961.00,257.00 961.00,257.00 Z M 571.00,225.00 C 571.00,225.00 562.98,249.00 562.98,249.00 562.98,249.00 552.58,283.00 552.58,283.00 552.58,283.00 549.00,295.00 549.00,295.00 549.00,295.00 569.00,295.00 569.00,295.00 569.00,295.00 570.00,263.00 570.00,263.00 570.00,263.00 571.00,246.00 571.00,246.00 571.00,246.00 571.00,225.00 571.00,225.00 Z M 360.00,431.00 C 368.53,422.39 373.82,415.45 385.00,409.49 409.15,396.60 446.11,398.83 467.00,417.18 473.43,422.83 479.63,429.41 483.68,437.00 483.68,437.00 492.00,456.00 492.00,456.00 492.00,456.00 495.59,434.00 495.59,434.00 496.07,431.16 496.51,426.78 498.70,424.74 500.63,422.93 503.53,423.04 506.00,423.00 506.00,423.00 539.00,423.00 539.00,423.00 541.87,423.01 545.35,422.71 547.72,424.60 551.32,427.46 553.46,442.92 554.58,448.00 554.58,448.00 563.92,494.00 563.92,494.00 565.22,501.64 568.07,519.79 571.00,526.00 571.00,526.00 573.91,492.00 573.91,492.00 573.91,492.00 575.29,473.00 575.29,473.00 575.29,473.00 580.58,440.00 580.58,440.00 581.09,436.52 581.79,427.94 583.85,425.43 586.06,422.74 589.86,423.02 593.00,423.00 593.00,423.00 625.00,423.00 625.00,423.00 627.50,423.00 632.30,422.62 634.01,424.74 635.46,426.54 634.99,431.77 634.71,434.00 634.71,434.00 630.59,459.00 630.59,459.00 630.59,459.00 612.25,577.00 612.25,577.00 612.25,577.00 608.08,603.00 608.08,603.00 607.55,606.20 606.79,613.17 604.91,615.57 602.93,618.10 599.91,617.96 597.00,618.00 597.00,618.00 563.00,618.00 563.00,618.00 560.52,618.00 556.51,618.24 554.43,616.83 550.84,614.40 548.94,598.71 548.00,594.00 548.00,594.00 538.80,544.00 538.80,544.00 537.38,536.91 535.95,526.25 533.00,520.00 532.22,536.89 526.29,577.06 523.41,595.00 522.63,599.83 521.69,614.48 517.72,616.98 515.77,618.21 512.27,618.00 510.00,618.00 510.00,618.00 467.00,618.00 467.00,618.00 467.00,618.00 477.58,548.00 477.58,548.00 477.58,548.00 483.00,514.00 483.00,514.00 473.01,525.51 463.41,536.00 449.00,541.99 441.10,545.27 439.14,545.36 431.00,546.75 428.09,547.25 423.68,547.83 421.83,550.42 419.43,553.77 422.67,557.28 422.96,564.00 423.29,571.52 419.31,584.27 417.37,592.00 415.20,600.69 410.17,623.19 406.72,630.00 403.13,637.12 398.08,640.40 390.00,639.82 390.00,639.82 377.00,637.52 377.00,637.52 373.09,636.52 368.33,634.78 365.18,632.21 359.93,627.93 358.79,621.43 359.04,615.00 359.04,615.00 367.13,579.00 367.13,579.00 369.02,571.44 372.50,553.48 377.39,548.21 379.73,545.68 381.82,544.94 385.00,544.00 379.31,531.51 369.69,531.66 359.23,515.00 354.20,506.99 350.98,498.19 348.89,489.00 348.08,485.46 347.61,478.92 346.30,476.04 343.82,470.58 339.09,471.01 334.00,471.00 329.97,470.99 320.27,470.42 317.06,472.17 311.28,475.32 310.29,490.79 310.00,497.00 310.00,497.00 346.00,497.00 346.00,497.00 346.00,497.00 341.54,531.00 341.54,531.00 341.02,533.78 339.63,541.17 337.47,542.84 334.77,544.91 327.38,544.99 324.00,545.00 324.00,545.00 302.00,545.00 302.00,545.00 302.00,545.00 293.92,597.00 293.92,597.00 293.92,597.00 290.00,617.00 290.00,617.00 280.85,618.21 276.98,617.96 268.00,618.00 268.00,618.00 254.00,619.00 254.00,619.00 254.00,619.00 245.00,619.00 245.00,619.00 243.06,619.00 239.28,619.25 237.74,618.01 235.59,616.27 236.11,612.39 236.43,610.00 236.43,610.00 239.92,588.00 239.92,588.00 239.92,588.00 255.92,486.00 255.92,486.00 255.92,486.00 263.08,443.00 263.08,443.00 263.71,439.19 264.88,428.05 266.93,425.43 269.19,422.55 273.71,423.01 277.00,423.00 277.00,423.00 340.00,423.00 340.00,423.00 355.70,423.00 359.43,421.33 360.00,431.00 Z M 414.00,422.29 C 398.55,424.84 385.42,430.31 376.55,444.00 355.31,476.79 377.05,521.53 417.00,522.00 426.73,522.11 435.09,520.85 444.00,516.68 474.12,502.58 481.35,462.13 458.83,438.04 447.54,425.97 430.16,420.76 414.00,422.29 Z M 652.10,423.74 C 657.64,422.34 682.67,422.92 690.00,423.00 690.00,423.00 699.00,423.83 699.00,423.83 714.65,425.33 729.45,427.11 743.00,436.10 761.60,448.45 770.74,471.25 771.00,493.00 771.50,535.89 756.92,588.81 715.00,608.68 700.69,615.47 685.69,617.82 670.00,618.00 670.00,618.00 660.00,618.82 660.00,618.82 660.00,618.82 650.00,618.00 650.00,618.00 650.00,618.00 628.00,618.00 628.00,618.00 625.70,618.00 621.58,618.38 619.97,616.40 618.17,614.05 619.46,607.79 619.97,605.00 619.97,605.00 624.42,576.00 624.42,576.00 624.42,576.00 627.73,557.00 627.73,557.00 627.73,557.00 642.58,462.00 642.58,462.00 642.58,462.00 646.05,438.00 646.05,438.00 646.70,432.17 646.73,427.06 652.10,423.74 Z M 868.00,572.00 C 868.00,572.00 863.13,603.00 863.13,603.00 862.64,606.48 861.85,613.97 859.30,616.40 857.26,618.35 853.63,617.99 851.00,618.00 851.00,618.00 783.00,618.00 783.00,618.00 767.88,618.00 761.45,620.21 762.17,611.00 762.17,611.00 764.72,594.00 764.72,594.00 764.72,594.00 770.92,553.00 770.92,553.00 770.92,553.00 784.75,466.00 784.75,466.00 784.75,466.00 789.27,437.00 789.27,437.00 789.73,433.88 790.50,426.88 792.72,424.74 794.89,422.65 799.17,423.00 802.00,423.00 802.00,423.00 880.00,423.00 880.00,423.00 883.02,423.00 887.85,422.51 889.70,425.43 891.59,428.42 889.47,437.47 888.80,441.00 888.80,441.00 885.58,461.00 885.58,461.00 885.10,463.62 884.57,467.43 882.49,469.26 880.30,471.19 876.74,470.99 874.00,471.00 874.00,471.00 839.00,471.00 839.00,471.00 839.00,471.00 836.00,496.00 836.00,496.00 836.00,496.00 875.00,496.00 875.00,496.00 875.00,496.00 868.00,543.00 868.00,543.00 868.00,543.00 841.00,543.00 841.00,543.00 837.78,543.04 831.97,543.25 829.56,545.58 826.63,548.41 823.90,565.23 823.00,570.00 823.00,570.00 850.00,571.00 850.00,571.00 850.00,571.00 868.00,572.00 868.00,572.00 Z M 873.00,617.00 C 873.00,617.00 879.58,575.00 879.58,575.00 879.58,575.00 892.57,495.00 892.57,495.00 892.57,495.00 896.43,468.00 896.43,468.00 896.43,468.00 901.27,437.00 901.27,437.00 901.80,433.56 902.71,427.01 905.43,424.74 907.88,422.69 911.97,423.00 915.00,423.00 915.00,423.00 936.00,423.00 936.00,423.00 950.97,423.00 959.92,422.91 975.00,424.72 975.00,424.72 983.00,425.44 983.00,425.44 1000.52,428.19 1017.39,437.15 1023.25,455.00 1025.96,463.26 1026.01,468.55 1026.00,477.00 1025.96,500.24 1012.61,531.22 988.00,537.00 988.00,537.00 1013.00,618.00 1013.00,618.00 1013.00,618.00 963.00,618.00 963.00,618.00 960.92,618.00 957.37,618.25 955.65,617.01 952.75,614.91 951.13,604.66 950.40,601.00 950.40,601.00 941.00,550.00 941.00,550.00 941.00,550.00 939.00,550.00 939.00,550.00 939.00,550.00 931.75,597.00 931.75,597.00 931.16,600.55 929.49,613.29 927.84,615.57 925.74,618.48 921.19,617.99 918.00,618.00 906.42,618.02 883.27,618.84 873.00,617.00 Z M 944.00,512.00 C 958.65,514.30 966.87,504.29 970.10,491.00 972.13,482.66 971.13,473.77 963.00,469.09 959.13,466.86 955.28,467.01 951.00,467.00 951.00,467.00 944.00,512.00 944.00,512.00 Z M 680.00,570.00 C 686.39,569.09 692.01,567.07 696.91,562.67 708.12,552.62 714.98,525.80 715.00,511.00 715.00,511.00 715.00,499.00 715.00,499.00 714.83,485.04 710.27,476.35 696.00,473.00 696.00,473.00 680.00,570.00 680.00,570.00 Z");

        configSplash.setOriginalHeight(1200); //in relation to your svg (path) resource
        configSplash.setOriginalWidth(1200); //in relation to your svg (path) resource
        configSplash.setAnimPathStrokeDrawingDuration(1500);
        configSplash.setPathSplashStrokeSize(5); //I advise value be <5
        configSplash.setPathSplashStrokeColor(R.color.white); //any color you want form colors.xml
        configSplash.setAnimPathFillingDuration(1500);
        configSplash.setPathSplashFillColor(R.color.black); //path object filling color


        //Customize Title
        configSplash.setTitleSplash("Find your desired sneakers");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(22f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
        configSplash.setTitleFont("fonts/poppins.ttf"); //provide string to your font located in assets/fonts/

    }

    @Override
    public void animationsFinished() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
