using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Web;

namespace AdmPanel.Models
{
    public class AppealContext : DbContext
    {
        public DbSet<Appeal> Appeals { get; set; }
        public DbSet<Ad> Ads { get; set; }
    }
}