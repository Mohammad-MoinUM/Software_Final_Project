# Mini Marketplace - Deployment Guide

## Deploying to Render

### Step 1: Prepare Your Repository

1. Ensure all code is pushed to your GitHub repository
2. Configure branch protection on `main` branch
3. Verify CI/CD pipeline is working

### Step 2: Create PostgreSQL Database on Render

1. Log in to [Render Dashboard](https://dashboard.render.com)
2. Click **New +** → **PostgreSQL**
3. Configure:
   - **Name:** `mini-marketplace-db`
   - **Database:** `mini_marketplace`
   - **User:** (auto-generated)
   - **Region:** Choose nearest to your users
   - **Plan:** Free or Starter
4. Click **Create Database**
5. Copy the **Internal Database URL** (starts with `postgresql://`)

### Step 3: Create Web Service

1. Click **New +** → **Web Service**
2. Connect your GitHub repository
3. Configure:
   - **Name:** `mini-marketplace`
   - **Region:** Same as database
   - **Branch:** `main`
   - **Runtime:** Docker
   - **Instance Type:** Free or Starter

### Step 4: Environment Variables

Add the following environment variables:

```
DATABASE_URL=<internal-database-url-from-step-2>
DDL_AUTO=update
SPRING_PROFILES_ACTIVE=prod
LOG_LEVEL=INFO
SHOW_SQL=false
```

### Step 5: Build Configuration

If not using Docker, configure:

- **Build Command:**
  ```bash
  mvn clean package -DskipTests
  ```

- **Start Command:**
  ```bash
  java -jar target/mini-marketplace-1.0.0.jar
  ```

### Step 6: Deploy

1. Click **Create Web Service**
2. Wait for build and deployment (5-10 minutes)
3. Monitor logs for any errors
4. Click on the generated URL to access your app

### Step 7: Verify Deployment

1. Check health endpoint: `https://your-app.onrender.com/actuator/health`
2. Test user registration
3. Test login functionality
4. Verify database connectivity

### Step 8: Set Up Auto-Deploy

1. Go to your web service settings
2. Find **Deploy Hook** URL
3. Go to GitHub repository → Settings → Secrets
4. Add new secret:
   - Name: `RENDER_DEPLOY_HOOK`
   - Value: (paste Deploy Hook URL)
5. GitHub Actions will now trigger deployments automatically

## Troubleshooting

### Application Won't Start

**Check:**
- Environment variables are correctly set
- Database URL is the internal URL
- Logs for specific error messages

### Database Connection Issues

**Solutions:**
- Verify DATABASE_URL format
- Ensure database is in the same region
- Check database is running and accessible

### Build Failures

**Common Causes:**
- Missing dependencies in pom.xml
- Test failures (check with -DskipTests if needed temporarily)
- Docker build errors

### Slow Performance

**Solutions:**
- Upgrade to paid tier
- Optimize database queries
- Add caching where appropriate
- Review and optimize DDL_AUTO setting

## Environment-Specific Settings

### Development
```properties
DDL_AUTO=update
SHOW_SQL=true
LOG_LEVEL=DEBUG
```

### Production
```properties
DDL_AUTO=validate
SHOW_SQL=false
LOG_LEVEL=INFO
```

## Monitoring

### Health Check

URL: `/actuator/health`

Expected Response:
```json
{
  "status": "UP"
}
```

### Logs

Access logs via Render Dashboard:
1. Go to your service
2. Click **Logs** tab
3. Monitor for errors or warnings

## Scaling

### Vertical Scaling
- Upgrade instance type in Render dashboard
- More CPU and RAM

### Horizontal Scaling
- Available on paid plans
- Configure number of instances
- Render handles load balancing

## Backup and Restore

### Database Backups

Render provides automatic backups on paid plans.

Manual backup:
1. Go to database dashboard
2. Click **Backup** button
3. Download backup file

### Restore from Backup

1. Create new database
2. Upload backup file
3. Update DATABASE_URL in web service

## Security Checklist

- [ ] All passwords are stored encrypted (BCrypt)
- [ ] No credentials in source code
- [ ] Environment variables used for sensitive data
- [ ] HTTPS enabled (automatic on Render)
- [ ] CSRF protection enabled
- [ ] SQL injection prevention (JPA handles this)
- [ ] Input validation on all endpoints
- [ ] Rate limiting considered for production

## Post-Deployment

1. Test all major features
2. Monitor logs for 24 hours
3. Set up error alerting
4. Document the live URL
5. Update README with deployment URL
6. Notify team members

## Support

For Render-specific issues:
- [Render Documentation](https://render.com/docs)
- [Render Community](https://community.render.com)

For application issues:
- Check GitHub Issues
- Review application logs
- Contact team members

---

**Remember:** Always test thoroughly in development before deploying to production!
