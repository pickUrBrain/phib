﻿using Microsoft.Kinect;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace KinectSkeleton
{
    static class Extensions
    {
        #region camera
        public static ImageSource ToBitmap(this ColorFrame frame)
        {
            FrameDescription fd = frame.FrameDescription;
            int width = frame.FrameDescription.Width;
            int height = frame.FrameDescription.Height;
            PixelFormat format = PixelFormats.Bgr32;

            byte[] colorPixels = new byte[width * height * ((format.BitsPerPixel + 7) / 8)];

            if (frame.RawColorImageFormat == ColorImageFormat.Bgra)
            {
                frame.CopyRawFrameDataToArray(colorPixels);
            }
            else
            {
                frame.CopyConvertedFrameDataToArray(colorPixels, ColorImageFormat.Bgra);
            }

            int stride = width * format.BitsPerPixel / 8;

            return BitmapSource.Create(width, height, 96, 96, format, null, colorPixels, stride);
        }
        #endregion

        #region camera
        public static WriteableBitmap newMethod(this ColorFrame frame)
        {
            FrameDescription fd = frame.FrameDescription;
            WriteableBitmap outputImg = new WriteableBitmap(fd.Width, fd.Height, 96.0, 96.0, PixelFormats.Bgra32, null);
            byte[] framePixels = new byte[fd.Width * fd.Height * 4];
            frame.CopyConvertedFrameDataToArray(framePixels, ColorImageFormat.Bgra);

            outputImg.Lock();
            Marshal.Copy(framePixels, 0, outputImg.BackBuffer, framePixels.Length);
            outputImg.AddDirtyRect(new Int32Rect(0, 0, fd.Width, fd.Height));
            outputImg.Unlock();
            return outputImg;
        }
        #endregion
    }
}