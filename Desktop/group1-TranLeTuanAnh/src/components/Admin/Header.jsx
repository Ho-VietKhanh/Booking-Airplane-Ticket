/* eslint-disable react/prop-types */

import { Layout, Button } from 'antd';
import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons';

const { Header } = Layout;
const AdminHeader = ({ collapsed, toggleCollapsed, adminUser }) => {
  return (
    <Header 
      style={{ 
        padding: '0 16px', 
        background: '#ffffff',
        display: 'flex', 
        alignItems: 'center',
        boxShadow: '0 1px 2px rgba(0, 0, 0, 0.1)',
        borderBottom: '1px solid #f0f0f0'
      }}
    >
      <Button
        type="text"
        icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        onClick={toggleCollapsed}
        style={{
          fontSize: '16px',
          width: 64,
          height: 64,
          color: '#000000',
        }}
      />
      <span style={{ marginLeft: '16px', color: '#000000' }}>
        Welcome, {adminUser?.fullName}
      </span>
    </Header>
  );
};

export default AdminHeader; 